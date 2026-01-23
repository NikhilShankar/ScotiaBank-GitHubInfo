package rebirth.nixaclabs.sbgithubinfo.ui.screens.main

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.repository.GithubDetailsRepository
import rebirth.nixaclabs.sbgithubinfo.networking.SBResponse
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: GithubDetailsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private val _sideEffect = Channel<MainScreenSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is MainScreenEvent.OnSearchClicked -> {
                onSearchClicked()
            }
            is MainScreenEvent.OnRepoSelected -> {
                onRepoSelected(event.repo)
            }
            is MainScreenEvent.OnClearSelectedRepo -> {
                _state.update { it.copy(selectedRepo = null) }
            }
        }
    }

    private fun onSearchClicked() {
        val userId = _state.value.searchQuery.trim()
        if (userId.isBlank()) return

        // Reset state for new search
        _state.update {
            it.copy(
                user = null,
                repos = emptyList(),
                userError = null,
                reposError = null,
                selectedRepo = null
            )
        }

        fetchUserInfo(userId)
        fetchUserRepos(userId)
    }

    private fun fetchUserInfo(userId: String) {
        viewModelScope.launch {
            repository.getUserInfo(userId).collect { response ->
                when (response) {
                    is SBResponse.Loading -> {
                        _state.update { it.copy(isLoadingUser = true, userError = null) }
                    }
                    is SBResponse.Success -> {
                        _state.update {
                            it.copy(isLoadingUser = false, user = response.data, userError = null)
                        }
                    }
                    is SBResponse.Error -> {
                        _state.update {
                            it.copy(isLoadingUser = false, userError = response.message)
                        }
                    }
                }
            }
        }
    }

    private fun fetchUserRepos(userId: String) {
        viewModelScope.launch {
            repository.getRepoList(userId).collect { response ->
                when (response) {
                    is SBResponse.Loading -> {
                        _state.update { it.copy(isLoadingRepos = true, reposError = null) }
                    }
                    is SBResponse.Success -> {
                        _state.update {
                            it.copy(isLoadingRepos = false, repos = response.data, reposError = null)
                        }
                    }
                    is SBResponse.Error -> {
                        _state.update {
                            it.copy(isLoadingRepos = false, reposError = response.message)
                        }
                    }
                }
            }
        }
    }

    private fun onRepoSelected(repo: GithubRepoDetails) {
        _state.update { it.copy(selectedRepo = repo) }
        viewModelScope.launch {
            _sideEffect.send(MainScreenSideEffect.NavigateToDetail)
        }
    }
}