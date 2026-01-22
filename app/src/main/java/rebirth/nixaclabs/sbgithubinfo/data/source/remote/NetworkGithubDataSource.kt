package rebirth.nixaclabs.sbgithubinfo.data.source.remote

import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser
import javax.inject.Inject

class NetworkGithubDataSource @Inject constructor(
    private val githubApiService: GithubApiService
) {

    suspend fun getUserInfo(userId: String): GithubUser {
        return githubApiService.getUserInfo(userId).toDomain()
    }

    suspend fun getUserRepos(userId: String): List<GithubRepoDetails> {
        return githubApiService.getUserRepos(userId).map { it.toDomain() }
    }
}
