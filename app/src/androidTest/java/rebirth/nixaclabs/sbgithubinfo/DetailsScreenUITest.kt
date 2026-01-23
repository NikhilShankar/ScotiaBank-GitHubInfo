package rebirth.nixaclabs.sbgithubinfo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.ui.screens.details.DetailsScreen
import rebirth.nixaclabs.sbgithubinfo.ui.theme.ScotiaBankGitHubInfoTheme

class DetailsScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testRepo = GithubRepoDetails(
        id = 1,
        name = "awesome-repo",
        description = "An awesome repository for testing",
        updatedAt = "2024-06-15T10:30:00Z",
        starGazersCount = 1500,
        forks = 300,
        watchersCount = 50,
        openIssuesCount = 10
    )


    /**
     * Test to verify all repo detail fields are displayed correctly
     */
    @Test
    fun detailsScreenDisplaysAllRepoFields() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                DetailsScreen(
                    repo = testRepo,
                    totalForks = 400,
                    hasStarBadge = false,
                    onBackClick = {}
                )
            }
        }

        // Verify repo name is displayed
        composeTestRule.onNodeWithText("awesome-repo").assertIsDisplayed()

        // Verify description is displayed
        composeTestRule.onNodeWithText("An awesome repository for testing").assertIsDisplayed()

        // Verify stargazers count is displayed
        composeTestRule.onNodeWithText("1500").assertIsDisplayed()

        // Verify forks count is displayed
        composeTestRule.onNodeWithText("300").assertIsDisplayed()

        // Verify section headers are displayed
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last Updated").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stargazers").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forks").assertIsDisplayed()
    }


    /**
     * Test to verify star badge is NOT displayed when totalForks <= 5000
     */
    @Test
    fun detailsScreenDoesNotDisplayStarBadgeWhenForksBelow5000() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                DetailsScreen(
                    repo = testRepo,
                    totalForks = 4999,
                    hasStarBadge = false,
                    onBackClick = {}
                )
            }
        }

        // Verify total forks value is displayed
        composeTestRule.onNodeWithTag("total_forks_value").assertIsDisplayed()

        // Verify star badge is NOT displayed
        composeTestRule.onAllNodesWithTag("star_badge").fetchSemanticsNodes().isEmpty()
    }


    /**
     * Test to verify star badge IS displayed when totalForks > 5000
     */
    @Test
    fun detailsScreenDisplaysStarBadgeWhenForksAbove5000() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                DetailsScreen(
                    repo = testRepo,
                    totalForks = 5001,
                    hasStarBadge = true,
                    onBackClick = {}
                )
            }
        }

        // Verify total forks value is displayed
        composeTestRule.onNodeWithTag("total_forks_value").assertIsDisplayed()
        composeTestRule.onNodeWithText("5001").assertIsDisplayed()

        // Verify star badge icons are displayed (there are 2 star icons)
        composeTestRule.onAllNodesWithTag("star_badge")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("star_badge")[1].assertIsDisplayed()

        // Verify star badge label is displayed
        composeTestRule.onNodeWithTag("star_badge_label").assertIsDisplayed()
    }


    /**
     * Test to verify "No repository selected" is shown when repo is null
     */
    @Test
    fun detailsScreenDisplaysNoRepoMessageWhenRepoIsNull() {
        composeTestRule.setContent {
            ScotiaBankGitHubInfoTheme {
                DetailsScreen(
                    repo = null,
                    totalForks = 0,
                    hasStarBadge = false,
                    onBackClick = {}
                )
            }
        }

        // Verify no repo message is displayed
        composeTestRule.onNodeWithText("No repository selected").assertIsDisplayed()
    }
}
