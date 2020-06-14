//package uz.mymax.million.notifications
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.os.Build
//import androidx.core.app.NotificationCompat
//
//class MediaNotificationManager(mediaService: MediaService) {
//    private val mMediaService: MediaService
//    val notificationManager: NotificationManager
//    private val mPlayAction: NotificationCompat.Action
//    private val mPauseAction: NotificationCompat.Action
//    private val mNextAction: NotificationCompat.Action
//    private val mPrevAction: NotificationCompat.Action
//
//    // Does nothing on versions of Android earlier than O.
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createChannel() {
//        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) { // The user-visible name of the channel.
//            val name: CharSequence = "MediaSession"
//            // The user-visible description of the channel.
//            val description = "MediaSession and MediaPlayer"
//            val importance = NotificationManager.IMPORTANCE_LOW
//            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
//            // Configure the notification channel.
//            mChannel.description = description
//            mChannel.enableLights(true)
//            // Sets the notification light color for notifications posted to this
//// channel, if the device supports this feature.
//            mChannel.lightColor = Color.RED
//            mChannel.enableVibration(true)
//            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//            notificationManager.createNotificationChannel(mChannel)
//            Log.d(
//                TAG,
//                "createChannel: New channel created"
//            )
//        } else {
//            Log.d(
//                TAG,
//                "createChannel: Existing channel reused"
//            )
//        }
//    }
//
//    private val isAndroidOOrHigher: Boolean
//        private get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
//
//    fun buildNotification(
//        @NonNull state: PlaybackStateCompat,
//        token: MediaSessionCompat.Token?,  //                                           boolean isPlaying,
//        description: MediaDescriptionCompat,
//        bitmap: Bitmap?
//    ): Notification {
//        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
//        // Create the (mandatory) notification channel when running on Android Oreo.
//        if (isAndroidOOrHigher) {
//            createChannel()
//        }
//        val builder: NotificationCompat.Builder =
//            Builder(mMediaService, CHANNEL_ID)
//        builder.setStyle(
//            MediaStyle()
//                .setMediaSession(token)
//                .setShowActionsInCompactView(0, 1, 2)
//        )
//            .setColor(ContextCompat.getColor(mMediaService, R.color.notification_bg))
//            .setSmallIcon(R.drawable.ic_audiotrack_grey_24dp) // Pending intent that is fired when user clicks on notification.
//            .setContentIntent(createContentIntent()) // Title - Usually Song name.
//            .setContentTitle(description.title) // Subtitle - Usually Artist name.
//            .setContentText(description.subtitle)
//            .setLargeIcon(bitmap) // When notification is deleted (when playback is paused and notification can be
//// deleted) fire MediaButtonPendingIntent with ACTION_STOP.
//            .setDeleteIntent(
//                MediaButtonReceiver.buildMediaButtonPendingIntent(
//                    mMediaService, PlaybackStateCompat.ACTION_STOP
//                )
//            ) // Show controls on lock screen even when user hides sensitive content.
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        // If skip to previous action is enabled.
//        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L) {
//            builder.addAction(mPrevAction)
//        }
//        builder.addAction(if (isPlaying) mPauseAction else mPlayAction)
//        // If skip to next action is enabled.
//        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L) {
//            builder.addAction(mNextAction)
//        }
//        return builder.build()
//    }
//
//    private fun createContentIntent(): PendingIntent {
//        val openUI = Intent(mMediaService, MainActivity::class.java)
//        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        return PendingIntent.getActivity(
//            mMediaService,
//            REQUEST_CODE,
//            openUI,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//    }
//
//    companion object {
//        private const val TAG = "MediaNotificationManage"
//        private const val CHANNEL_ID =
//            "com.codingwithmitch.spotifyclone.musicplayer.channel"
//        private const val REQUEST_CODE = 101
//        const val NOTIFICATION_ID = 201
//    }
//
//    init {
//        mMediaService = mediaService
//        notificationManager =
//            mMediaService.getSystemService(Context.NOTIFICATION_SERVICE)
//        mPlayAction = Action(
//            R.drawable.ic_play_arrow_white_24dp,
//            mMediaService.getString(R.string.label_play),
//            MediaButtonReceiver.buildMediaButtonPendingIntent(
//                mMediaService,
//                PlaybackStateCompat.ACTION_PLAY
//            )
//        )
//        mPauseAction = Action(
//            R.drawable.ic_pause_circle_outline_white_24dp,
//            mMediaService.getString(R.string.label_pause),
//            MediaButtonReceiver.buildMediaButtonPendingIntent(
//                mMediaService,
//                PlaybackStateCompat.ACTION_PAUSE
//            )
//        )
//        mNextAction = Action(
//            R.drawable.ic_skip_next_white_24dp,
//            mMediaService.getString(R.string.label_next),
//            MediaButtonReceiver.buildMediaButtonPendingIntent(
//                mMediaService,
//                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
//            )
//        )
//        mPrevAction = Action(
//            R.drawable.ic_skip_previous_white_24dp,
//            mMediaService.getString(R.string.label_previous),
//            MediaButtonReceiver.buildMediaButtonPendingIntent(
//                mMediaService,
//                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
//            )
//        )
//        // cancel all previously shown notifications
//        notificationManager.cancelAll()
//    }
//}