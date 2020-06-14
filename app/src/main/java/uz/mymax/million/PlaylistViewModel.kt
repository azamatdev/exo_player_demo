package uz.mymax.million

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.*
import uz.mymax.million.data.Data
import uz.mymax.million.data.Mp3Item
import uz.mymax.million.data.Resource
import uz.mymax.million.utils.Event

class PlaylistViewModel : ViewModel() {
    private var _playlist = MutableLiveData<List<Mp3Item>>()
    var playlist: LiveData<List<Mp3Item>> = _playlist


    init {
        _playlist.value = Data.getPlaylistMp3()
    }

    fun showEqualizerVisibility(currentIndex: Int) {
        Log.d("TagCheck", "Current Index: $currentIndex");
        _playlist.value!![currentIndex].isPlaying.postValue(true)

        for( i in _playlist.value!!.indices){
            if(i != currentIndex){
                hideEqualizerVisibility(i)
            }
        }


    }
    fun hideEqualizerVisibility(currentIndex: Int) {
        Log.d("TagCheck", "Current Index hide: $currentIndex");
        _playlist.value!![currentIndex].isPlaying.postValue(false)
    }
}