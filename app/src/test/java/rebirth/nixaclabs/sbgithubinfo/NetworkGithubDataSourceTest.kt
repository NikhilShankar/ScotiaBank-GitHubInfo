package rebirth.nixaclabs.sbgithubinfo

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import rebirth.nixaclabs.sbgithubinfo.data.source.remote.GithubApiService
import rebirth.nixaclabs.sbgithubinfo.data.source.remote.NetworkGithubDataSource
import rebirth.nixaclabs.sbgithubinfo.data.source.remote.RepoFetchException
import rebirth.nixaclabs.sbgithubinfo.data.source.remote.dto.GithubRepoDto
import rebirth.nixaclabs.sbgithubinfo.data.source.remote.dto.GithubUserDto

class NetworkGithubDataSourceTest {

    private lateinit var apiService: GithubApiService
    private lateinit var dataSource: NetworkGithubDataSource

    private val testUserDto = GithubUserDto(name = "John Doe", avatarUrl = "https://avatar.url")

    private fun createRepoDto(
        id: Int,
        name: String = "repo$id",
        isPrivate: Boolean = false
    ) = GithubRepoDto(
        id = id,
        name = name,
        description = "Description $id",
        updatedAt = "2024-01-01",
        stargazersCount = 100,
        forks = 50,
        watchersCount = 10,
        openIssuesCount = 5,
        isPrivate = isPrivate
    )

    @Before
    fun setUp() {
        apiService = mockk()
        dataSource = NetworkGithubDataSource(apiService)
    }

    @Test
    fun `getUserInfo should return user from API`() = runTest {
        coEvery { apiService.getUserInfo("testuser") } returns testUserDto

        val result = dataSource.getUserInfo("testuser")

        assertThat(result.name).isEqualTo("John Doe")
        assertThat(result.avatarUrl).isEqualTo("https://avatar.url")
    }

    @Test
    fun `getUserReposFlow should emit repos on success`() = runTest {
        val repos = listOf(createRepoDto(1), createRepoDto(2))
        coEvery { apiService.getUserRepos("testuser", 1, 100) } returns repos

        dataSource.getUserReposFlow("testuser").test {
            val emittedRepos = awaitItem()
            assertThat(emittedRepos).hasSize(2)
            assertThat(emittedRepos[0].name).isEqualTo("repo1")
            assertThat(emittedRepos[1].name).isEqualTo("repo2")
            awaitComplete()
        }
    }


    /**
     * Ideally the public github api will return only public repositories.
     * The requirement specifically asks for public repos hence added the functionality to filter private repos
     * This is testing the same.
     */
    @Test
    fun `getUserReposFlow should filter out private repos`() = runTest {
        val repos = listOf(
            createRepoDto(1, "public-repo", isPrivate = false),
            createRepoDto(2, "private-repo", isPrivate = true),
            createRepoDto(3, "another-public", isPrivate = false)
        )
        coEvery { apiService.getUserRepos("testuser", 1, 100) } returns repos

        dataSource.getUserReposFlow("testuser").test {
            val emittedRepos = awaitItem()
            assertThat(emittedRepos).hasSize(2)
            assertThat(emittedRepos.map { it.name }).containsExactly("public-repo", "another-public")
            awaitComplete()
        }
    }

    @Test
    fun `getUserReposFlow should paginate when repos exceed page size`() = runTest {
        // First page - 100 repos (full page, indicates more pages)
        val page1Repos = (1..100).map { createRepoDto(it) }
        // Second page - 50 repos (less than 100, indicates last page)
        val page2Repos = (101..150).map { createRepoDto(it) }

        coEvery { apiService.getUserRepos("testuser", 1, 100) } returns page1Repos
        coEvery { apiService.getUserRepos("testuser", 2, 100) } returns page2Repos

        dataSource.getUserReposFlow("testuser").test {
            // First emission after page 1
            val firstEmission = awaitItem()
            assertThat(firstEmission).hasSize(100)

            // Second emission after page 2 (cumulative)
            val secondEmission = awaitItem()
            assertThat(secondEmission).hasSize(150)

            awaitComplete()
        }
    }


    /**
     * This is a crucial test case which asserts if an error properly retries for atleast 2 times.
     * If at the third attempt api succeeds then it should return success and return the repos.
     */
    @Test
    fun `getUserReposFlow should retry with backoff on failure then succeed`() = runTest {
        val repos = listOf(createRepoDto(1), createRepoDto(2))
        var callCount = 0

        coEvery { apiService.getUserRepos("testuser", 1, 100) } answers {
            callCount++
            if (callCount < 3) {
                throw RuntimeException("Network error")
            }
            repos
        }

        dataSource.getUserReposFlow("testuser").test {
            val emittedRepos = awaitItem()
            assertThat(emittedRepos).hasSize(2)
            assertThat(callCount).isEqualTo(3) // Failed twice, succeeded on third
            awaitComplete()
        }
    }


    /**
     * On a failure it should emit error only if the third attempt also fails
     */
    @Test
    fun `getUserReposFlow should throw RepoFetchException after max retries exhausted`() = runTest {
        coEvery { apiService.getUserRepos("testuser", 1, 100) } throws RuntimeException("Network error")

        dataSource.getUserReposFlow("testuser").test {
            val error = awaitError()
            assertThat(error).isInstanceOf(RepoFetchException::class.java)
            assertThat(error.message).contains("Failed to fetch page 1 after 3 retries")
        }
    }
}
