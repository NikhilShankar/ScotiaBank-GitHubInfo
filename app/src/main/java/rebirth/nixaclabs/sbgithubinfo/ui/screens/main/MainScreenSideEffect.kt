package rebirth.nixaclabs.sbgithubinfo.ui.screens.main

sealed class MainScreenSideEffect {
    data object NavigateToDetail : MainScreenSideEffect()
    data class ShowError(val message: String) : MainScreenSideEffect()
}