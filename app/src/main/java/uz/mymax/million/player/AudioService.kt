package uz.mymax.million.player

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.mymax.million.BuildConfig
import uz.mymax.million.MainActivity
import uz.mymax.million.R
import uz.mymax.million.data.Data
import uz.mymax.million.data.Mp3Item
import java.util.*

private const val PLAYBACK_CHANNEL_ID = "playback_channel"
private const val PLAYBACK_NOTIFICATION_ID = 1
private const val MEDIA_SESSION_TAG = "sed_audio"

private const val PLAYBACK_TIMER_DELAY = 5 * 1000L

private const val ARG_AUDIO_INDEX = "audio_index"
private const val ARG_TITLE = "title"
private const val ARG_START_POSITION = "start_position"

class AudioService : LifecycleService() {

    inner class AudioServiceBinder : Binder() {
        val service
            get() = this@AudioService

        val exoPlayer
            get() = this@AudioService.exoPlayer
    }

    companion object {

        @MainThread
        fun newIntent(context: Context, audioItem: Mp3Item? = null) =
            Intent(context, AudioService::class.java).apply {
                audioItem?.let {
                    putExtra(ARG_AUDIO_INDEX, audioItem.index)
                    putExtra(ARG_TITLE, audioItem.title)
                }
            }

    }


    private var playbackTimer: Timer? = null

    var audioIndex: Int = 0
        private set
    private var audioTitle: String? = null

    private lateinit var exoPlayer: SimpleExoPlayer

    private var playerNotificationManager: PlayerNotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null

    private val _playerStatusLiveData = MutableLiveData<PlayerStatus>()
    val playerStatusLiveData: LiveData<PlayerStatus>
        get() = _playerStatusLiveData

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MOVIE)
            .build()
        exoPlayer.setAudioAttributes(audioAttributes, true)

        // Monitor ExoPlayer events.
        exoPlayer.addListener(PlayerEventListener())

        // Setup notification and media session.
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            applicationContext,
            PLAYBACK_CHANNEL_ID,
            R.string.playback_channel_name,
            PLAYBACK_NOTIFICATION_ID,
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): String {
                    return audioTitle ?: getString(R.string.loading_dots)
                }

                @Nullable
                override fun createCurrentContentIntent(player: Player): PendingIntent? =
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        Intent(applicationContext, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                @Nullable
                override fun getCurrentContentText(player: Player): String? {
                    return null
                }

                @Nullable
                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return getBitmapFromVectorDrawable(applicationContext, Data.getLargeIcon(audioIndex))
                }
            },
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationStarted(
                    notificationId: Int,
                    notification: Notification?
                ) {
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(notificationId: Int) {
                    _playerStatusLiveData.value = PlayerStatus.Cancelled(audioIndex)

                    stopSelf()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification?,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        // Make sure the service will not get destroyed while playing media.
                        startForeground(notificationId, notification)
                    } else {
                        // Make notification cancellable.
                        stopForeground(false)
                    }
                }
            }
        ).apply {
            // Omit skip previous and next actions.
            setUseNavigationActions(false)

            // Add stop action.
            setUseStopAction(true)

            val incrementMs = resources.getInteger(R.integer.increment_ms).toLong()
            setFastForwardIncrementMs(incrementMs)
            setRewindIncrementMs(incrementMs)

            setPlayer(exoPlayer)
        }

        // Show lock screen controls and let apps like Google assistant manager playback.
        mediaSession = MediaSessionCompat(applicationContext, MEDIA_SESSION_TAG).apply {
            isActive = true
        }
        playerNotificationManager?.setMediaSessionToken(mediaSession?.sessionToken)

        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(
                    player: Player,
                    windowIndex: Int
                ): MediaDescriptionCompat {
                    val bitmap =
                        getBitmapFromVectorDrawable(applicationContext, Data.getLargeIcon(audioIndex))
                    val extras = Bundle().apply {
                        putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                        putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
                    }

                    val title = audioTitle ?: getString(R.string.loading_dots)

                    return MediaDescriptionCompat.Builder()
                        .setIconBitmap(bitmap)
                        .setTitle(title)
                        .setExtras(extras)
                        .build()
                }
            })

            val incrementMs = resources.getInteger(R.integer.increment_ms)
            setFastForwardIncrementMs(incrementMs)
            setRewindIncrementMs(incrementMs)

            setPlayer(exoPlayer)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)

        handleIntent(intent)

        return AudioServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        cancelPlaybackMonitor()

        mediaSession?.release()
        mediaSessionConnector?.setPlayer(null)
        playerNotificationManager?.setPlayer(null)

        exoPlayer.release()

        super.onDestroy()
    }

    @MainThread
    private fun handleIntent(intent: Intent?) {
        intent?.let {
            audioIndex = intent.getIntExtra(ARG_AUDIO_INDEX, 0)
            audioTitle = intent.getStringExtra(ARG_TITLE)
            val startPosition =
                intent.getLongExtra(ARG_START_POSITION, C.POSITION_UNSET.toLong())
            play(startPosition, audioIndex)
        }
    }

    @MainThread
    fun play(startPosition: Long, index: Int) {
        Log.d("ExoTag", "PLay")
        val userAgent = Util.getUserAgent(applicationContext, BuildConfig.APPLICATION_ID)
        val haveStartPosition = startPosition != C.POSITION_UNSET.toLong()
        if (haveStartPosition) {
            exoPlayer.seekTo(startPosition)
        }
        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(Data.getRawList()[index]))
        val dataSourceFactory = DefaultDataSourceFactory(
            applicationContext,
            Util.getUserAgent(applicationContext, userAgent)
        )

        val mediaSource = ProgressiveMediaSource.Factory { dataSourceFactory.createDataSource() }
            .setTag(index)
            .createMediaSource(dataSpec.uri)
        exoPlayer.prepare(mediaSource, !haveStartPosition, false)
        exoPlayer.playWhenReady = true
    }


    @MainThread
    fun resume() {
        exoPlayer.playWhenReady = true
    }

    @MainThread
    fun pause() {
        exoPlayer.playWhenReady = false
    }

    @MainThread
    private fun saveLastListeningPosition() = lifecycleScope.launch {
        //        episodeId?.let { appDatabase.listenedDao().insert(Listened(it, exoPlayer.contentPosition, exoPlayer.duration)) }
    }

    @MainThread
    private fun monitorPlaybackProgress() {
        if (playbackTimer == null) {
            playbackTimer = Timer()

            playbackTimer?.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        saveLastListeningPosition()

                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                if (exoPlayer.duration - exoPlayer.contentPosition <= PLAYBACK_TIMER_DELAY) {
                                    playbackTimer?.cancel()
                                }
                            }
                        }
                    }
                },
                PLAYBACK_TIMER_DELAY,
                PLAYBACK_TIMER_DELAY
            )
        }
    }

    @MainThread
    private fun cancelPlaybackMonitor() {
        saveLastListeningPosition()

        playbackTimer?.cancel()
        playbackTimer = null
    }

    @MainThread
    private fun getBitmapFromVectorDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap? {
        return ContextCompat.getDrawable(context, drawableId)?.let {
            val drawable = DrawableCompat.wrap(it).mutate()

            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            bitmap
        }
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                if (exoPlayer.playWhenReady) {
                    audioIndex.let { _playerStatusLiveData.value = PlayerStatus.Playing(it) }
                } else {// Paused
                    audioIndex.let { _playerStatusLiveData.value = PlayerStatus.Paused(it) }
                }
            } else if (playbackState == Player.STATE_ENDED) {
                audioIndex.let { _playerStatusLiveData.value = PlayerStatus.Ended(it) }
            } else {
                audioIndex.let { _playerStatusLiveData.value = PlayerStatus.Other(it) }
            }

            // Only monitor playback to record progress when playing.
            if (playbackState == Player.STATE_READY && exoPlayer.playWhenReady) {
                monitorPlaybackProgress()
            } else {
                cancelPlaybackMonitor()
            }
        }

        override fun onPlayerError(e: ExoPlaybackException?) {
            audioIndex.let { _playerStatusLiveData.value = PlayerStatus.Error(it, e) }
        }

    }

}