package rebirth.nixaclabs.sbgithubinfo.ui.screens.main

import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser

data class MainScreenState(
    val searchQuery: String = "",
    val isLoadingUser: Boolean = false,
    val isLoadingRepos: Boolean = false,
    val user: GithubUser? = null,
    val repos: List<GithubRepoDetails> = emptyList(),
    val userError: String? = null,
    val reposError: String? = null,
    val selectedRepo: GithubRepoDetails? = null
) {
    val totalForks: Int
        get() = repos.sumOf { it.forks }

    val hasStarBadge: Boolean
        get() = totalForks > 5000
}