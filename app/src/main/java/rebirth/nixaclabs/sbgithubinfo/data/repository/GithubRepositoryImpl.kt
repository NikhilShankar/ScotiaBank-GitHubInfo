package rebirth.nixaclabs.sbgithubinfo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import rebirth.nixaclabs.sbgithubinfo.data.source.remote.NetworkGithubDataSource
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser
import rebirth.nixaclabs.sbgithubinfo.domain.repository.GithubDetailsRepository
import rebirth.nixaclabs.sbgithubinfo.networking.SBResponse
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkGithubDataSource
) : GithubDetailsRepository {

    override fun getUserInfo(userId: String): Flow<SBResponse<GithubUser>> = flow {
        emit(SBResponse.Loading)
        val user = networkDataSource.getUserInfo(userId)
        emit(SBResponse.Success(user))
    }.catch { e ->
        emit(SBResponse.Error(e.message ?: "Failed to fetch user info"))
    }

    override fun getRepoList(userId: String): Flow<SBResponse<List<GithubRepoDetails>>> =
        networkDataSource.getUserReposFlow(userId)
            .map<List<GithubRepoDetails>, SBResponse<List<GithubRepoDetails>>> { repos ->
                SBResponse.Success(repos)
            }
            .onStart { emit(SBResponse.Loading) }
            .catch { e ->
                emit(SBResponse.Error(e.message ?: "Failed to fetch repositories"))
            }
}
