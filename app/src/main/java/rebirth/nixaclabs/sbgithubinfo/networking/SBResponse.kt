package rebirth.nixaclabs.sbgithubinfo.networking

sealed class SBResponse<out T> {
    
    data class Success<T>(val data: T) : SBResponse<T>()

    data class Error(val message: String) : SBResponse<Nothing>()

    object Loading : SBResponse<Nothing>()

}
