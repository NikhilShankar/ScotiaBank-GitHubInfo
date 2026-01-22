package rebirth.nixaclabs.sbgithubinfo.data.source.remote

import rebirth.nixaclabs.sbgithubinfo.data.source.remote.dto.GithubRepoDto
import rebirth.nixaclabs.sbgithubinfo.data.source.remote.dto.GithubUserDto
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {

    @GET("users/{userId}")
    suspend fun getUserInfo(@Path("userId") userId: String): GithubUserDto

    @GET("users/{userId}/repos")
    suspend fun getUserRepos(@Path("userId") userId: String): List<GithubRepoDto>

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}
