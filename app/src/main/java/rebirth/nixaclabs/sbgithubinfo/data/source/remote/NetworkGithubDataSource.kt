package rebirth.nixaclabs.sbgithubinfo.data.source.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser
import javax.inject.Inject

class NetworkGithubDataSource @Inject constructor(
    private val githubApiService: GithubApiService
) {
    companion object {
        private const val PER_PAGE = 100
        private const val MAX_RETRIES = 3
        private const val INITIAL_BACKOFF_MS = 2000L

        private const val MAXIMUM_BACKOFF_MS = 16000L
    }

    suspend fun getUserInfo(userId: String): GithubUser {
        return githubApiService.getUserInfo(userId).toDomain()
    }

    fun getUserReposFlow(userId: String): Flow<List<GithubRepoDetails>> = flow {
        val allRepos = mutableListOf<GithubRepoDetails>()
        var currentPage = 1

        while (true) {
            val pageRepos = fetchPageWithRetry(userId, currentPage)

            if (pageRepos == null) {
                // All retries exhausted, throw exception (partial data already emitted)
                throw RepoFetchException("Failed to fetch page $currentPage after $MAX_RETRIES retries")
            }

            allRepos.addAll(pageRepos)
            emit(allRepos.toList())

            if (pageRepos.size < PER_PAGE) {
                // Last page reached
                break
            }
            currentPage++
        }
    }

    private suspend fun fetchPageWithRetry(userId: String, page: Int): List<GithubRepoDetails>? {

        repeat(MAX_RETRIES) { attempt ->
            try {
                /**
                 * Added filter for private repos as per the requirement.
                 * So only public repos will be displayed.
                 */
                return githubApiService.getUserRepos(userId, page, PER_PAGE)
                    .filter { it.isPrivate == false }.map { it.toDomain() }
            } catch (e: Exception) {
                if (attempt < MAX_RETRIES - 1) {
                    // Exponential backoff: 1s, 2s, 4s
                    val backoffMs = INITIAL_BACKOFF_MS * (1L shl attempt).coerceAtMost(MAXIMUM_BACKOFF_MS)
                    delay(backoffMs)
                }
            }
        }
        return null
    }
}

class RepoFetchException(message: String) : Exception(message)
