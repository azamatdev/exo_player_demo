//package uz.mymax.million.temp
//
//import uz.mymax.million.PlaylistViewModel
//import uz.mymax.million.R
//
//import android.content.Context
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import com.google.android.material.snackbar.Snackbar
//import kotlinx.android.synthetic.main.fragment_home.*
//import org.koin.androidx.viewmodel.ext.android.viewModel
//import uz.mymax.million.adapter.PlaylistAdapter
//import uz.mymax.million.data.Data
//import uz.mymax.million.data.Mp3Item
//import uz.mymax.million.player.PlayerCallback
//import uz.mymax.million.player.PlayerStatus
//
//
//class HomeFragmentTemp : Fragment(R.layout.fragment_home) {
//    private lateinit var adapter: PlaylistAdapter
//
//    //Player Variables
////    private var player: SimpleExoPlayer? = null
////    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
////    private var playWhenReadyPlayer = true
////    private var playbackPosition: Long = 0
////    private var currentWindow = 0
////    private var window = -1;
//    private val viewModel: PlaylistViewModel by viewModel()
//
//    private var playerCallback: PlayerCallback? = null
////    private lateinit var playerNotificationManager: PlayerNotificationManager
//
////    companion object {
////         var CHANNEL_ID = "player_channel_id"
////         var NOTIFICATION_ID = 1996
////    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initRecycler()
//
//        renderMp3Item(viewModel.playlist.value!![0])
//
//    }
//
//    private fun renderMp3Item(mp3Item : Mp3Item){
//
//        if (playerCallback == null ) {
////            hidePlayerViews
//        } else {
//            monitorPlayback(mp3Item)
//            playerCallback?.play(mp3Item)
//
////            playButton.setOnClickListener {
////                viewModel.markEpisodeAsListened(episode._id)
////
////                showStopViews()
////                playerCallback?.play(episode)
////
////                monitorPlayback(episode)
////            }
////            stopButton.setOnClickListener {
////                showPlayViews()
////                playerCallback?.stop()
////
////                playerCallback?.playerStatusLiveData?.removeObservers(this)
////            }
//        }
//    }
//
//    private fun monitorPlayback(mp3Item: Mp3Item) {
//        playerCallback?.playerStatusLiveData?.removeObservers(this)
//        playerCallback?.playerStatusLiveData?.observe(viewLifecycleOwner, Observer { playerStatus ->
//            if (mp3Item.index == playerStatus.index) {
//                when (playerStatus) {
//                    is PlayerStatus.Playing -> showEqualizer(mp3Item.index)
//                    is PlayerStatus.Paused -> hideEqualizer(mp3Item.index)
//                    is PlayerStatus.Ended -> hideEqualizer(mp3Item.index)
//                    is PlayerStatus.Cancelled -> hideEqualizer(mp3Item.index)
//                    is PlayerStatus.Error -> acknowledgeGenericError()
//                }
//            }
//        })
//    }
//    private fun showEqualizer(index : Int){
//        viewModel.showEqualizerVisibility(index)
//    }
//    private fun hideEqualizer(index : Int){
//        viewModel.hideEqualizerVisibility(index)
//    }
//
//    fun acknowledgeGenericError()
//            = Snackbar.make(requireView(), "Xatolik yuz berdi", Snackbar.LENGTH_SHORT).show()
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//        playerCallback = context as? PlayerCallback
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//
//        playerCallback = null
//    }
//
//    //    private fun updateEqualizerView(window: Int) {
////        //find the last view from the recyclerView
//////        var lastView = player_recycler.findViewHolderForAdapterPosition(window)
//////        lastView.
////    }
//
//    private fun onItemCLick(position: Int) {
////        log("Position: $position \n\nWindow: $window")
////        if (window == position) {
////            player?.playWhenReady = !player!!.playWhenReady
////        } else {
////            updatePositions(position)
////            player?.seekTo(position, C.TIME_UNSET)
////            player?.playWhenReady = true
////        }
////        if (window > 0)
////            viewModel.changeEqualizerVisibility(window, position)
////        else
////            viewModel.changeEqualizerVisibility(0, position)
////        playerCallback.play()
//        playerCallback?.play(Data.getPlaylistMp3()[position])
//    }
//
//    private fun updatePositions(position : Int){
////        if (window > 0)
////            viewModel.changeEqualizerVisibility(window, position)
////        else
////            viewModel.changeEqualizerVisibility(0, position)
////        window = position
////        player?.playWhenReady = true
//    }
//    /**
//     * Exo Player Logic
//     */
////    private fun initExoPlayer() {
////        playerNotificationManager = PlayerNotificationManager(
////            context,
////            CHANNEL_ID,
////            NOTIFICATION_ID,
////            DescriptionAdapter(context!!)
////            )
////        if (player == null) {
////            player = ExoPlayerFactory.newSimpleInstance(
////                context,
////                DefaultRenderersFactory(context),
////                DefaultTrackSelector(),
////                DefaultLoadControl()
////            )
////        }
////        //To create Playlist music
////        concatenatingMediaSource = Data.getReadyPlaylistMediaSource(context = context!!)
////        player?.prepare(concatenatingMediaSource)
////        player?.addListener(PlaybackStateListener())
////        player?.seekTo(currentWindow, playbackPosition)
////        player?.playWhenReady = playWhenReadyPlayer
////        playerView.player = player
////        playerNotificationManager.setPlayer(player)
////        playerNotificationManager.setNotificationListener(object : PlayerNotificationManager.NotificationListener {
////            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
////                super.onNotificationCancelled(notificationId, dismissedByUser)
////                playerNotificationManager.setPlayer(null)
////                releasePlayer()
////            }
////
////            override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
////                super.onNotificationStarted(notificationId, notification)
////            }
////
////            override fun onNotificationPosted(
////                notificationId: Int,
////                notification: Notification?,
////                ongoing: Boolean
////            ) {
////                super.onNotificationPosted(notificationId, notification, ongoing)
////                log("NotificationId: $notificationId", "playerTag")
////                log("OnGoing: $ongoing", "playerTag")
////            }
////        })
////        playerNotificationManager.setRewindIncrementMs(0);
////        playerNotificationManager.setFastForwardIncrementMs(0);
////
////    }
////
////    private fun releasePlayer() {
////        if (player != null) {
////            currentWindow = player!!.currentWindowIndex
////            playbackPosition = player!!.currentPosition
////            playWhenReadyPlayer = player!!.playWhenReady
////            player!!.release()
////            player = null
////        }
////    }
////
////    /**
////     * Lifecylce Handle for Player
////     */
////    override fun onStart() {
////        super.onStart()
////        if (Util.SDK_INT > 23) {
////            initExoPlayer()
////        }
////    }
////
////    override fun onResume() {
////        super.onResume()
////        if (Util.SDK_INT <= 23 || player == null) {
////            initExoPlayer()
////        }
////    }
////
////    override fun onPause() {
////        super.onPause()
////        if (Util.SDK_INT <= 23) {
////            releasePlayer()
////        }
////    }
////
////    override fun onStop() {
////        super.onStop()
////        if (Util.SDK_INT > 23) {
////            releasePlayer()
////        }
////    }
////
////    override fun onDestroy() {
////        playerNotificationManager.setPlayer(null)
////        super.onDestroy()
////    }
////
//    private fun initRecycler() {
//        adapter = PlaylistAdapter(
//            viewModel,
//            viewLifecycleOwner
//        ) { position: Int -> onItemCLick(position) }
//        player_recycler.adapter = adapter
//        adapter.updateList(viewModel.playlist.value!!)
//    }
//
//
////    private inner class PlaybackStateListener : Player.EventListener {
////
////        override fun onPlayerError(error: ExoPlaybackException?) {
////            super.onPlayerError(error)
//////            Log.d("MainActivity", "TacksGroup: " + error?.message)
////        }
////
////        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
////            super.onPlaybackParametersChanged(playbackParameters)
//////            Log.d("MainActivity", "TacksGroup: " + playbackParameters.)
////        }
////
////
////        override fun onPositionDiscontinuity(reason: Int) {
////            super.onPositionDiscontinuity(reason)
////            if (reason == DISCONTINUITY_REASON_SEEK) {
////                log("Reason: $reason", "OtherTag")
////                if (player?.currentTag != null) {
////                    updatePositions(player!!.currentWindowIndex);
////                    // Seek within current playlist item.
////                }
////
////            }
////        }
////
////        private fun handleDiscontinuity(reason: Int) {
////            if (reason == DISCONTINUITY_REASON_PERIOD_TRANSITION) {
////                if (window == player?.currentWindowIndex) {
////                    // Repeating the same playlist item automatically.
////                } else {
////                    // Moved to next playlist item automatically.
////                }
////            } else if (reason == DISCONTINUITY_REASON_SEEK) {
////                if (window == player?.currentWindowIndex) {
////                    // Seek within current playlist item.
////                } else {
////                    // Seek to another playlist item (e.g. skipping to next or previous)
////                }
////            } else if (reason == DISCONTINUITY_REASON_AD_INSERTION) {
////                // Transitioned from content to ad or from ad to content within current playlist item.
////            } else {
////                // Position jumped discontinuously within current playlist item. For example because
////                // seek got adjusted or for other internal reason.
////            }
////            window = player!!.currentWindowIndex;
////        }
////
////
////        override fun onPlayerStateChanged(
////            playWhenReady: Boolean,
////            playbackState: Int
////        ) {
////            val stateString: String
////            when (playbackState) {
////                ExoPlayer.STATE_IDLE -> stateString = "ExoPlayer.STATE_IDLE      -"
////                ExoPlayer.STATE_BUFFERING -> stateString = "ExoPlayer.STATE_BUFFERING -"
////                ExoPlayer.STATE_READY -> stateString = "ExoPlayer.STATE_READY     -"
////                ExoPlayer.STATE_ENDED -> stateString = "ExoPlayer.STATE_ENDED     -"
////                else -> stateString = "UNKNOWN_STATE             -"
////            }
//////            Log.d(
//////                "MainActivity", "changed state to " + stateString
//////                        + " playWhenReady: " + playWhenReady
//////            )
////        }
////    }
//
//
//
//
//}
