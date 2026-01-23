package rebirth.nixaclabs.sbgithubinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import rebirth.nixaclabs.sbgithubinfo.ui.navigation.GithubNavGraph
import rebirth.nixaclabs.sbgithubinfo.ui.theme.ScotiaBankGitHubInfoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScotiaBankGitHubInfoTheme {
                val navController = rememberNavController()
                GithubNavGraph(navController = navController)
            }
        }
    }
}