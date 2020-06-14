package uz.mymax.million.player

sealed class PlayerStatus(open val index: Int ) {
    data class Other(override val index: Int = 0) : PlayerStatus(index)
    data class Playing(override val index: Int) : PlayerStatus(index)
    data class Paused(override val index: Int) : PlayerStatus(index)
    data class Cancelled(override val index: Int) : PlayerStatus(index)
    data class Ended(override val index: Int) : PlayerStatus(index)
    data class Error(override val index: Int, val exception: Exception?) : PlayerStatus(index)
}