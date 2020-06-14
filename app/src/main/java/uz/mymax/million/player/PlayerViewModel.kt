package uz.mymax.million.player

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.*
import uz.mymax.million.data.Data
import uz.mymax.million.data.Mp3Item
import uz.mymax.million.utils.Event

class PlayerViewModel : ViewModel(){
    private val audioIndexLiveData = MutableLiveData<Pair<Int, Boolean>>()
    val audioResource: LiveData<Mp3Item> =
        Transformations.switchMap(audioIndexLiveData) { (audioIndex, forcePlay) ->
            liveData {


                if (forcePlay) {
                    _playMediaLiveData.postValue(Event(Data.getPlaylistMp3()[audioIndex]))
                }
                Log.d("CheckService","Emitting audio Index")
                emit(Data.getPlaylistMp3()[audioIndex])
            }
        }

    private val _playMediaLiveData = MutableLiveData<Event<Mp3Item>>()
    val playMediaLiveData: LiveData<Event<Mp3Item>>
        get() = _playMediaLiveData


    @MainThread
    fun refreshIfNecessary(audioIndex: Int) {
        if (audioIndexLiveData.value == null) {
            audioIndexLiveData.value = Pair(audioIndex, false)
        }
    }

    @MainThread
    fun play(audioIndex: Int) {
        Log.d("CheckService","Played")
        audioIndexLiveData.value = Pair(audioIndex, true)
    }

}