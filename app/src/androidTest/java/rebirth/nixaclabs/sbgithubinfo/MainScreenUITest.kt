package rebirth.nixaclabs.sbgithubinfo

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreen
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenEvent
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenState
import rebirth.nixaclabs.sbgithubinfo.ui.theme.ScotiaBankGitHubInfoTheme

class MainScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUser = GithubUser(name = "John Doe", avatarUrl = "https://avatar.url")

    private val testRepos = listOf(
        GithubRepoDetails(
            id = 1,
            name = "awesome-repo",
            description = "An awesome repository",
            updatedAt = "2024-01-01",
            starGazersCount = 100,
            forks = 50,
            watchersCount = 10,
            openIssuesCount = 5
        ),
        GithubRepoDetails(
            id = 2,
            name = "cool-project",
            description = "A cool project",
            updatedAt = "2024-01-02",
            starGazersCount = 200,
            forks = 100,
            watchersCount = 20,
            openIssuesCount = 10
        )
    )


    /**
     * Test to verify that the search button and text field label are displayed
     * on initial screen load
     */
    @Test
    fun searchButtonAndTextFieldAreDisplayed() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(),
                    onEvent = {}
                )
            }
        }

        // Verify search button is displayed
        composeTestRule.onNodeWithText("SEARCH").assertIsDisplayed()

        // Verify text field label is displayed
        composeTestRule.onNodeWithText("Enter a github user id").assertIsDisplayed()
    }


    /**
     * Test to verify that typing in the search field triggers the correct event
     * with the typed text
     */
    @Test
    fun typingInSearchFieldTriggersOnSearchQueryChangedEvent() {
        val capturedEvents = mutableListOf<MainScreenEvent>()

        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(),
                    onEvent = { capturedEvents.add(it) }
                )
            }
        }

        // Type in the search field
        composeTestRule.onNodeWithText("Enter a github user id").performTextInput("testuser")

        // Verify the events were captured (one event per character typed)
        assertTrue("Expected events to be captured", capturedEvents.isNotEmpty())
        assertTrue(
            "Expected OnSearchQueryChanged events",
            capturedEvents.filterIsInstance<MainScreenEvent.OnSearchQueryChanged>().isNotEmpty()
        )
    }


    /**
     * Test to verify that user info section displays the user's name
     * when a user is loaded in the state
     */
    @Test
    fun userInfoIsDisplayedWhenUserIsLoaded() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(user = testUser),
                    onEvent = {}
                )
            }
        }

        // Verify user name is displayed
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    }


    /**
     * Test to verify that the repo list displays repository names and descriptions
     * when repos are available in the state
     */
    @Test
    fun repoListDisplaysReposWhenAvailable() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(repos = testRepos),
                    onEvent = {}
                )
            }
        }

        // Verify repo names are displayed
        composeTestRule.onNodeWithText("awesome-repo").assertIsDisplayed()
        composeTestRule.onNodeWithText("cool-project").assertIsDisplayed()

        // Verify repo descriptions are displayed
        composeTestRule.onNodeWithText("An awesome repository").assertIsDisplayed()
        composeTestRule.onNodeWithText("A cool project").assertIsDisplayed()
    }


    /**
     * Test to verify that the repo list displays the exact number of cards
     * matching the number of repos in the state
     */
    @Test
    fun repoListDisplaysExactCountOfCardsWhenAvailable() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(repos = testRepos),
                    onEvent = {}
                )
            }
        }

        // Verify exact count of repo cards matches testRepos size (2)
        composeTestRule.onAllNodesWithTag("repo_card").assertCountEquals(2)
    }


    /**
     * Test to verify that the repo cards are displayed in the same order
     * as provided in the state
     */
    @Test
    fun repoListDisplaysRepoCardsInExactOrder() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(repos = testRepos),
                    onEvent = {}
                )
            }
        }

        val repoCards = composeTestRule.onAllNodesWithTag("repo_card")

        // Verify first card contains first repo's name
        repoCards[0].assertTextContains("awesome-repo")

        // Verify second card contains second repo's name
        repoCards[1].assertTextContains("cool-project")
    }


    /**
     * Test to verify that loading is shown when isLoadingUser or isLoadingRepos is true
     */
    @Test
    fun mainScreenDisplaysLoadingIndicatorForUsersWhenLoading() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(isLoadingUser = true, isLoadingRepos = true),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("main_screen_loading_user").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main_screen_loading_repos").assertIsDisplayed()
    }


    /**
     * Test to verify that error is shown when userError is not null and repo is loading
     */
    @Test
    fun mainScreenDisplaysError() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(isLoadingUser = false, userError = "Failed", isLoadingRepos = true),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("main_screen_user_error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main_screen_loading_repos").assertIsDisplayed()
    }


    /**
     * Test to verify that error is shown when userError is not null and repo is loading
     */
    @Test
    fun mainScreenDisplaysErrorForRepo() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(isLoadingUser = true, reposError = "Failed", isLoadingRepos = false),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("main_screen_repos_error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main_screen_loading_user").assertIsDisplayed()
    }


    /**
     * Test to verify that clicking on a repo card triggers OnRepoSelected event
     * with the correct repo details
     */
    @Test
    fun clickingOnRepoCardTriggersOnRepoSelectedEvent() {
        val capturedEvents = mutableListOf<MainScreenEvent>()

        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                MainScreen(
                    state = MainScreenState(repos = testRepos),
                    onEvent = { capturedEvents.add(it) }
                )
            }
        }

        // Click on the first repo card
        composeTestRule.onAllNodesWithTag("repo_card")[0].performClick()

        // Verify OnRepoSelected event was triggered with correct repo
        val repoSelectedEvents = capturedEvents.filterIsInstance<MainScreenEvent.OnRepoSelected>()
        assertTrue("Expected OnRepoSelected event", repoSelectedEvents.isNotEmpty())
        assertTrue(
            "Expected first repo to be selected",
            repoSelectedEvents.first().repo.id == testRepos[0].id
        )
    }
}
