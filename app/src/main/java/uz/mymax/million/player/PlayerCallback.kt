package uz.mymax.million.player

import androidx.lifecycle.LiveData
import uz.mymax.million.data.Mp3Item

interface PlayerCallback {
    val playerStatusLiveData : LiveData<PlayerStatus>

    fun play(mp3Item: Mp3Item)

    fun stop()

    fun pauseOrResumePlayer()

    fun getPlayerPosition() : Int?
}