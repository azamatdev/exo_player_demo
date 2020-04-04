package uz.mymax.million

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.util.Util
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.sample.*
import uz.mymax.million.adapter.PlaylistAdapter
import uz.mymax.million.data.Data


class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var adapter : PlaylistAdapter

    //Player Variables
    private var player : SimpleExoPlayer? = null
    private lateinit var concatenatingMediaSource : ConcatenatingMediaSource
    private var playWhenReadyPlayer = true
    private var playbackPosition : Long = 0
    private var currentWindow = 0
    private var previousPosition = -1;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()

        initExoPlayer()

        Log.d("MainActivity", "Satagfeaf")
        customUiClicks()

        toolbar_layout.setOnClickListener {
            slidingPanelLayout.panelState = PanelState.ANCHORED
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (slidingPanelLayout != null &&
                        (slidingPanelLayout.panelState == PanelState.EXPANDED || slidingPanelLayout.panelState == PanelState.ANCHORED)
                    ) {
                        slidingPanelLayout.panelState = PanelState.COLLAPSED;
                    }
                }
            });

    }

    private fun onItemCLick(position : Int){
        if(previousPosition >= 0){
            val previousView = player_recycler.findViewHolderForAdapterPosition(previousPosition)
            val previousEqualizer = previousView!!.itemView.findViewById<LottieAnimationView>(R.id.equalizer)
            previousEqualizer.visibility = View.GONE
        }

        val view = player_recycler.findViewHolderForAdapterPosition(position)
        val equalizer = view!!.itemView.findViewById<LottieAnimationView>(R.id.equalizer)
        equalizer.visibility = View.VISIBLE

        if(previousPosition == position){
            player?.playWhenReady = !player!!.playWhenReady
        }else{
            previousPosition = position
            player?.seekTo(position, C.TIME_UNSET)
            player?.playWhenReady = true
        }
    }

    /**
     * Exo Player Logic
     */
    private fun initExoPlayer() {
        if(player == null ){
            player = ExoPlayerFactory.newSimpleInstance(
                context,
                DefaultTrackSelector(),
                DefaultLoadControl()
            )
        }
        //To create Playlist music
        concatenatingMediaSource = Data.getReadyPlaylistMediaSource(context = context!!)
        player?.prepare(concatenatingMediaSource)
        player?.seekTo(currentWindow, playbackPosition)
//        player?.playWhenReady = playWhenReadyPlayer
        player_view.player = player

    }

    private fun releasePlayer(){
        if (player != null ) {
            currentWindow = player!!.currentWindowIndex
            playbackPosition = player!!.currentPosition
            playWhenReadyPlayer = player!!.playWhenReady
            player!!.release()
            player = null
        }
    }

    /**
     * Lifecylce Handle for Player
     */
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initExoPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initExoPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }
    private fun initRecycler() {
        adapter = PlaylistAdapter(){position : Int -> onItemCLick(position)}
        player_recycler.adapter = adapter
        adapter.updateList(Data.getPlaylistMp3())
    }


    private inner class PlaybackStateListener : Player.EventListener {

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            super.onPlaybackParametersChanged(playbackParameters)

        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
            super.onTracksChanged(trackGroups, trackSelections)
            Log.d("MainActivity", "TacksGroup: " + trackGroups?.length)
            Log.d("MainActivity", "TrackSelection: " + trackSelections?.length)
            Log.d("MainActivity", "TrackSelection: " + trackSelections?.all.toString())
        }

        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            val stateString: String
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> stateString = "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> stateString = "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> stateString = "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> stateString = "ExoPlayer.STATE_ENDED     -"
                else -> stateString = "UNKNOWN_STATE             -"
            }
            Log.d(
                "MainActivity", "changed state to " + stateString
                        + " playWhenReady: " + playWhenReady
            )
        }
    }

    private fun customUiClicks() {
//        like.setOnClickListener(View.OnClickListener {
//            dislike.visibility = View.VISIBLE
//            Toast.makeText(context, "You Like the Song", Toast.LENGTH_SHORT).show()
//            if (dislike.visibility == View.VISIBLE) {
//                dislike.visibility = View.GONE
//            }
//        })
//
//        dislike.setOnClickListener(View.OnClickListener { dislike.setVisibility(View.GONE) })
//
//        dislike.setOnClickListener(View.OnClickListener {
//            dislike.visibility = View.VISIBLE
//            Toast.makeText(context, "You DisLike the Song", Toast.LENGTH_SHORT).show()
//            if (dislike.visibility == View.VISIBLE) {
//                dislike.visibility = View.GONE
//            }
//        })
//
//        dislike.setOnClickListener(View.OnClickListener { dislike.setVisibility(View.GONE) })
//
//        play_button_main.setOnClickListener(View.OnClickListener {
//            play_button_main.visibility = View.GONE
//            play_button_main.visibility = View.VISIBLE
//            Toast.makeText(context, "Song Is now Playing", Toast.LENGTH_SHORT).show()
//            if (play_button_main.visibility == View.VISIBLE) {
//                play_button_main.visibility = View.GONE
//                play_button_main.visibility = View.VISIBLE
//            }
//        })
//
//        pause_button.setOnClickListener(View.OnClickListener {
//            pause_button.visibility = View.GONE
//            pause_button.visibility = View.VISIBLE
//            Toast.makeText(context, "Song is Pause", Toast.LENGTH_SHORT).show()
//            if (pause_button.visibility == View.VISIBLE) {
//                pause_button.visibility = View.GONE
//                pause_button.visibility = View.VISIBLE
//            }
//        })
//
//        play_button.setOnClickListener(View.OnClickListener {
//            play_button.setVisibility(View.GONE)
//            play_button.setVisibility(View.VISIBLE)
//            Toast.makeText(context, "Song Is now Playing", Toast.LENGTH_SHORT).show()
//            if (play_button.visibility == View.VISIBLE) {
//                play_button.visibility = View.GONE
//                play_button.setVisibility(View.VISIBLE)
//            }
//        })
//
//        pause_button_main.setOnClickListener(View.OnClickListener {
//            pause_button_main.setVisibility(View.GONE)
//            pause_button_main.setVisibility(View.VISIBLE)
//            Toast.makeText(context, "Song is Pause", Toast.LENGTH_SHORT).show()
//            if (pause_button_main.visibility == View.VISIBLE) {
//                pause_button_main.visibility = View.GONE
//                pause_button_main.visibility = View.VISIBLE
//            }
//        })

    }



}

fun Fragment.log(message: String, TAG : String){
    Log.d(TAG, message)
}