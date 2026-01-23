package rebirth.nixaclabs.sbgithubinfo

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser
import rebirth.nixaclabs.sbgithubinfo.domain.repository.GithubDetailsRepository
import rebirth.nixaclabs.sbgithubinfo.networking.SBResponse
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenEvent
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenSideEffect
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTests {

    private lateinit var repository: GithubDetailsRepository
    private lateinit var viewModel: MainScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testUser = GithubUser(name = "John Doe", avatarUrl = "https://avatar.url")
    private val testRepos = listOf(
        GithubRepoDetails(
            id = 1,
            name = "repo1",
            description = "Description 1",
            updatedAt = "2024-01-01",
            starGazersCount = 100,
            forks = 50,
            watchersCount = 10,
            openIssuesCount = 5
        ),
        GithubRepoDetails(
            id = 2,
            name = "repo2",
            description = "Description 2",
            updatedAt = "2024-01-02",
            starGazersCount = 200,
            forks = 100,
            watchersCount = 20,
            openIssuesCount = 10
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() {
        viewModel = MainScreenViewModel(repository)

        val state = viewModel.state.value

        assertThat(state.searchQuery).isEmpty()
        assertThat(state.isLoadingUser).isFalse()
        assertThat(state.isLoadingRepos).isFalse()
        assertThat(state.user).isNull()
        assertThat(state.repos).isEmpty()
        assertThat(state.userError).isNull()
        assertThat(state.reposError).isNull()
        assertThat(state.selectedRepo).isNull()
    }

    @Test
    fun `onSearchQueryChanged should update searchQuery in state`() {
        viewModel = MainScreenViewModel(repository)

        viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged("testuser"))

        assertThat(viewModel.state.value.searchQuery).isEqualTo("testuser")
    }

    @Test
    fun `onSearchClicked should NOT trigger search when query is blank or whitespace`() {
        viewModel = MainScreenViewModel(repository)

        // Test empty query
        viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged(""))
        viewModel.onEvent(MainScreenEvent.OnSearchClicked)

        // Test whitespace only
        viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged("   "))
        viewModel.onEvent(MainScreenEvent.OnSearchClicked)

        // Verify repository methods were never called
        verify(exactly = 0) { repository.getUserInfo(any()) }
        verify(exactly = 0) { repository.getRepoList(any()) }
    }

    @Test
    fun `onSearchClicked success should update user and repos in state`() = runTest {
        every { repository.getUserInfo("testuser") } returns flow {
            emit(SBResponse.Loading)
            emit(SBResponse.Success(testUser))
        }
        every { repository.getRepoList("testuser") } returns flow {
            emit(SBResponse.Loading)
            emit(SBResponse.Success(testRepos))
        }

        viewModel = MainScreenViewModel(repository)

        viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged("testuser"))
        viewModel.onEvent(MainScreenEvent.OnSearchClicked)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.user).isEqualTo(testUser)
        assertThat(state.repos).isEqualTo(testRepos)
        assertThat(state.isLoadingUser).isFalse()
        assertThat(state.isLoadingRepos).isFalse()
        assertThat(state.userError).isNull()
        assertThat(state.reposError).isNull()
    }

    @Test
    fun `onSearchClicked error should set userError and reposError`() = runTest {
        every { repository.getUserInfo("testuser") } returns flow {
            emit(SBResponse.Loading)
            emit(SBResponse.Error("User not found"))
        }
        every { repository.getRepoList("testuser") } returns flow {
            emit(SBResponse.Loading)
            emit(SBResponse.Error("Failed to fetch repos"))
        }

        viewModel = MainScreenViewModel(repository)

        viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged("testuser"))
        viewModel.onEvent(MainScreenEvent.OnSearchClicked)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.userError).isEqualTo("User not found")
        assertThat(state.reposError).isEqualTo("Failed to fetch repos")
        assertThat(state.isLoadingUser).isFalse()
        assertThat(state.isLoadingRepos).isFalse()
        assertThat(state.user).isNull()
        assertThat(state.repos).isEmpty()
    }

    @Test
    fun `onRepoSelected should update selectedRepo and emit NavigateToDetail`() = runTest {
        viewModel = MainScreenViewModel(repository)
        val selectedRepo = testRepos[0]

        viewModel.sideEffect.test {
            viewModel.onEvent(MainScreenEvent.OnRepoSelected(selectedRepo))

            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(viewModel.state.value.selectedRepo).isEqualTo(selectedRepo)
            assertThat(awaitItem()).isEqualTo(MainScreenSideEffect.NavigateToDetail)
        }
    }

    @Test
    fun `rapid consecutive searches should cancel previous jobs`() = runTest {
        // First search - slow response with error
        every { repository.getUserInfo("wronguser") } returns flow {
            emit(SBResponse.Loading)
            delay(1000) // Simulate slow response
            emit(SBResponse.Error("User not found"))
        }
        every { repository.getRepoList("wronguser") } returns flow {
            emit(SBResponse.Loading)
            delay(1000)
            emit(SBResponse.Error("Repos not found"))
        }

        // Second search - fast response with success
        every { repository.getUserInfo("correctuser") } returns flow {
            emit(SBResponse.Loading)
            emit(SBResponse.Success(testUser))
        }
        every { repository.getRepoList("correctuser") } returns flow {
            emit(SBResponse.Loading)
            emit(SBResponse.Success(testRepos))
        }

        viewModel = MainScreenViewModel(repository)

        // First search
        viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged("wronguser"))
        viewModel.onEvent(MainScreenEvent.OnSearchClicked)

        // Advance a little but not enough for first search to complete
        testDispatcher.scheduler.advanceTimeBy(100)

        // Second search (should cancel first)
        viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged("correctuser"))
        viewModel.onEvent(MainScreenEvent.OnSearchClicked)

        // Advance until all coroutines complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Should have results from second search, not errors from first
        val state = viewModel.state.value
        assertThat(state.user).isEqualTo(testUser)
        assertThat(state.repos).isEqualTo(testRepos)
        assertThat(state.userError).isNull()
        assertThat(state.reposError).isNull()
    }
}
