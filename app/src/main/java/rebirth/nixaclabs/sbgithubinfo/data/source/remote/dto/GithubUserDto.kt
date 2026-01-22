package rebirth.nixaclabs.sbgithubinfo.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser

@Serializable
data class GithubUserDto(
    @SerialName("name")
    val name: String?,
    @SerialName("avatar_url")
    val avatarUrl: String?
) {
    fun toDomain(): GithubUser {
        return GithubUser(
            name = name ?: "",
            avatarUrl = avatarUrl ?: ""
        )
    }
}
