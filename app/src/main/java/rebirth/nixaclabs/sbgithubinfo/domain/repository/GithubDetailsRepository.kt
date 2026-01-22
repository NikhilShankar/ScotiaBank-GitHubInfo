package rebirth.nixaclabs.sbgithubinfo.domain.repository

import kotlinx.coroutines.flow.Flow
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser
import rebirth.nixaclabs.sbgithubinfo.networking.SBResponse

interface GithubDetailsRepository {

    fun getUserInfo(): Flow<SBResponse<GithubUser>>

    fun getRepoList(): Flow<SBResponse<List<GithubRepoDetails>>>

}