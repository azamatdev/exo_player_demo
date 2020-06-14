package uz.mymax.million.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop

import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.player_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import uz.mymax.million.R
import uz.mymax.million.data.Mp3Item
import uz.mymax.million.utils.*

abstract class BasePlayerActivity : AppCompatActivity(), PlayerCallback {


    private inner class PlayerWindowCallback(val originalCallback: Window.Callback) :
        BaseWindowCallback(originalCallback) {

        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            // Collapse the bottom sheet if touch will not be handle by the bottom sheet view.
//            if (event.action == MotionEvent.ACTION_UP &&
//                findViewById<View>(R.id.stopButton)?.pointInView(event.x, event.y) == false &&
//                !playerOverlayContainerConstraintLayout.pointInView(event.x, event.y)) {
//                collapsePlayerOverlay()
//            }
            return super.dispatchTouchEvent(event)
        }
    }


    private var audioService: AudioService? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("CheckService", "Service Connected")
            val binder = service as AudioService.AudioServiceBinder
            audioService = binder.service

            // Attach the ExoPlayer to the PlayerView.
            playerView.player = binder.exoPlayer


            // Pass player updates to interested observers.
            audioService?.playerStatusLiveData?.observe(this@BasePlayerActivity, Observer {
                _playerStatusLiveData.postValue(it)

                playerOverlayPlayMaterialButton.isSelected = it is PlayerStatus.Playing

                if (it is PlayerStatus.Cancelled) {
                    dismissPlayerOverlay()

                    stopAudioService()
                }
            })

            // Show player after config change.
            val episodeId = audioService?.audioIndex
            if (episodeId != null) {
                showPlayerOverlay()

//                viewModel.refreshIfNecessary(episodeId)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
        }
    }


    private val _playerStatusLiveData: MutableLiveData<PlayerStatus> = MutableLiveData()
    override val playerStatusLiveData: LiveData<PlayerStatus>
        get() = _playerStatusLiveData

    private val viewModel: PlayerViewModel by viewModel()


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setupPlayerBottomSheet()

        playerOverlayPlayMaterialButton.setOnClickListener {
            if (playerOverlayPlayMaterialButton.isSelected) {
                audioService?.pause()
            } else {
                audioService?.resume()
            }
        }


        viewModel.audioResource.observe(this, Observer { audioItem ->
            renderContent(audioItem)
        })

        viewModel.playMediaLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { audio ->
                AudioService.newIntent(this, audio).also { intent ->
                    // This service will get converted to foreground service using the PlayerNotificationManager notification Id.
                    startService(intent)
                }
            }
        })

        playerView.showController()
    }

    override fun onStart() {
        super.onStart()

        // Show the player, if the audio service is already running.
        if (applicationContext.isServiceRunning(AudioService::class.java.name)) {
            bindToAudioService()
        } else {
            dismissPlayerOverlay()
        }
    }

    override fun onStop() {
        unbindAudioService()
        super.onStop()
    }


    override fun play(mp3Item: Mp3Item) {
        showPlayerOverlay()

        bindToAudioService()

        viewModel.play(mp3Item.index)
    }

    override fun stop() {
        dismissPlayerOverlay()

        audioService?.audioIndex?.let { episodeId ->
            _playerStatusLiveData.value = PlayerStatus.Paused(episodeId)
        } ?: run {
            _playerStatusLiveData.value = PlayerStatus.Other()
        }
        stopAudioService()
    }

    override fun pauseOrResumePlayer() {
        if (playerOverlayPlayMaterialButton.isSelected) {
            audioService?.pause()
        } else {
            audioService?.resume()
        }
    }

    override fun getPlayerPosition(): Int? {
        return audioService?.audioIndex
    }

    private fun bindToAudioService() {
        if (audioService == null) {
            AudioService.newIntent(this).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun unbindAudioService() {
        if (audioService != null) {
            unbindService(connection)

            audioService = null
        }
    }

    private fun stopAudioService() {
        audioService?.pause()

        unbindAudioService()
        stopService(Intent(this, AudioService::class.java))

        audioService = null
    }

    private fun setupPlayerBottomSheet() {
//        dismissPlayerOverlay()

        toolbar_layout.setOnClickListener {
            togglePlayerOverlayShowState()
        }

//        BottomSheetBehavior.from(playerOverlayContainerConstraintLayout).apply {
//            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) = when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> hidePlayerOverlayPlaceHolder()
//                    BottomSheetBehavior.STATE_EXPANDED -> showPlayerOverlayPlaceHolder()
//                    BottomSheetBehavior.STATE_COLLAPSED -> showPlayerOverlayPlaceHolder()
//                    else -> { }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                }
//            })
//        }
    }

    private fun renderContent(audio: Mp3Item) {
        Log.d("CheckService", "Rendering content From Service")

        val audioTitle = audio.title ?: getString(R.string.loading_dots)

        songs_title.text = audioTitle

//        playerView.findViewById<TextView>(R.id.titleTextView).text = episodeTitle

        Glide.with(this)
            .load(audio.image)
//            .transform(MultiTransformation(CenterCrop(), CircleCrop()))
            .placeholder(R.drawable.vd_image)
            .into(audio_image_small)

        Glide.with(this)
            .load(audio.image)
            .transform(MultiTransformation(CircleCrop()))
            .placeholder(R.drawable.vd_image)
            .into(image_center)
    }


    private fun togglePlayerOverlayShowState() {
        if (mainSlidingPanelLayout != null &&
            (mainSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED
                    || mainSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED)
        ) {
            mainSlidingPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            mainSlidingPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }
    }

    private fun showPlayerOverlay() {

        mainSlidingPanelLayout.panelHeight = dpToPx(70f).toInt()
        layoutSlidingPanel.show()

    }

    private fun dismissPlayerOverlay() {

        if (mainSlidingPanelLayout != null &&
            (mainSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED
                    || mainSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED)
        ) {
            mainSlidingPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
        mainSlidingPanelLayout.panelHeight = 0
        layoutSlidingPanel.hide()

    }

//    private fun showPlayerOverlayPlaceHolder() {
//        (mainSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED || mainSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED)
//    }
//
//    private fun hidePlayerOverlayPlaceHolder() {
//        mainSlidingPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
//    }

    fun dpToPx(valueInDp: Float): Float {
        val metrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }


}