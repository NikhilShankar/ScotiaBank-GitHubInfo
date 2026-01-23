package rebirth.nixaclabs.sbgithubinfo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import rebirth.nixaclabs.sbgithubinfo.ui.screens.details.DetailsScreenUI
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenUI
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenViewModel

object NavRoutes {
    const val GITHUB_FLOW = "github_flow"
    const val MAIN = "main"
    const val DETAIL = "detail"
}

@Composable
fun GithubNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.GITHUB_FLOW
    ) {
        navigation(
            startDestination = NavRoutes.MAIN,
            route = NavRoutes.GITHUB_FLOW
        ) {
            composable(NavRoutes.MAIN) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavRoutes.GITHUB_FLOW)
                }
                val viewModel: MainScreenViewModel = hiltViewModel(parentEntry)

                MainScreenUI(
                    viewModel = viewModel,
                    onNavigateToDetail = {
                        navController.navigate(NavRoutes.DETAIL)
                    }
                )
            }

            composable(NavRoutes.DETAIL) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavRoutes.GITHUB_FLOW)
                }
                val viewModel: MainScreenViewModel = hiltViewModel(parentEntry)

                DetailsScreenUI(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
