package rebirth.nixaclabs.sbgithubinfo.ui.screens.main

import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails

sealed class MainScreenEvent {
    data class OnSearchQueryChanged(val query: String) : MainScreenEvent()
    data object OnSearchClicked : MainScreenEvent()
    data class OnRepoSelected(val repo: GithubRepoDetails) : MainScreenEvent()
    data object OnClearSelectedRepo : MainScreenEvent()
}