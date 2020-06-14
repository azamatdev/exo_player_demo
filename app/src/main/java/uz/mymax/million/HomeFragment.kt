package uz.mymax.million

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import uz.mymax.million.adapter.PlaylistAdapter
import uz.mymax.million.data.Data
import uz.mymax.million.data.Mp3Item
import uz.mymax.million.player.PlayerCallback
import uz.mymax.million.player.PlayerStatus


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val adapter: PlaylistAdapter by inject()

    private val viewModel: PlaylistViewModel by viewModel()

    private var playerCallback: PlayerCallback? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        monitorPlayback();
//        renderMp3Item(viewModel.playlist.value!![0])

    }

    private fun initRecycler() {
        adapter.viewModel = viewModel
        adapter.viewLifecycleOwner = viewLifecycleOwner
        adapter.clickListener = this::onItemCLick
        player_recycler.adapter = adapter
        adapter.updateList(viewModel.playlist.value!!)
    }

    private fun onItemCLick(position: Int) {
        if(playerCallback?.getPlayerPosition() == position){
            playerCallback?.pauseOrResumePlayer()
        }else{
            playerCallback?.play(Data.getPlaylistMp3()[position])
        }
    }


    private fun renderMp3Item(mp3Item: Mp3Item) {

        if (playerCallback == null) {
//            hidePlayerViews
        } else {
//            monitorPlayback(mp3Item)
            playerCallback?.play(mp3Item)
        }
    }

    private fun monitorPlayback() {
//        playerCallback?.playerStatusLiveData?.removeObservers(this)
        playerCallback?.playerStatusLiveData?.observe(viewLifecycleOwner, Observer { playerStatus ->
            Log.d("ServiceCheck", "Status: ${playerStatus.index}" )
//            if (mp3Item.index == playerStatus.index) {
                when (playerStatus) {
                    is PlayerStatus.Playing -> viewModel.showEqualizerVisibility(playerStatus.index)
                    is PlayerStatus.Paused -> viewModel.hideEqualizerVisibility(playerStatus.index)
                    is PlayerStatus.Ended -> viewModel.hideEqualizerVisibility(playerStatus.index)
                    is PlayerStatus.Cancelled -> viewModel.hideEqualizerVisibility(playerStatus.index)
                    is PlayerStatus.Error -> Log.d("ServiceCheck", "Error")
                }
//            }
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        playerCallback = context as? PlayerCallback
    }

    override fun onDetach() {
        super.onDetach()
        playerCallback = null
    }


}
