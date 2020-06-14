package uz.mymax.million.data


sealed class Resource<out T: Any> {
    object Loading : Resource<Nothing>()
    object RequireLogin : Resource<Nothing>()
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Error(val exception: Exception, val isConnected: Boolean) : Resource<Nothing>()
}

