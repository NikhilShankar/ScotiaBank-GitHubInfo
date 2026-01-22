package rebirth.nixaclabs.sbgithubinfo.domain.model

data class GithubRepoDetails(
    val id: Int,
    val name: String,
    val description: String,
    val updatedAt: String,
    val starGazersCount: Int,
    val forks: Int,
    val watchersCount: Int,
    val openIssuesCount: Int
)