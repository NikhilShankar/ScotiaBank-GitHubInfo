package rebirth.nixaclabs.sbgithubinfo.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails

@Serializable
data class GithubRepoDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("stargazers_count")
    val stargazersCount: Int?,
    @SerialName("forks")
    val forks: Int?,
    @SerialName("watchers_count")
    val watchersCount: Int?,
    @SerialName("open_issues_count")
    val openIssuesCount: Int?
) {
    fun toDomain(): GithubRepoDetails {
        return GithubRepoDetails(
            id = id,
            name = name ?: "",
            description = description ?: "",
            updatedAt = updatedAt ?: "",
            starGazersCount = stargazersCount ?: 0,
            forks = forks ?: 0,
            watchersCount = watchersCount ?: 0,
            openIssuesCount = openIssuesCount ?: 0
        )
    }
}
