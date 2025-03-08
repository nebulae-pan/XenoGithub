package io.nebula.xenogithub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.nebula.xenogithub.biz.model.Repository
import io.nebula.xenogithub.biz.model.User
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.ui.utils.CommonUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by nebula on 2025/3/7
 */
class UserProfilerViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    ViewModel() {
    private val TAG = "AccountViewModel"
    private val _uiState = MutableStateFlow(UserProfilerUIState())
    val uiState: StateFlow<UserProfilerUIState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.toString())
    }

    fun loadUserRepos(user: User?) {
        if (user == null) {
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            XenoNet.loadReposFromUser(user.token ?: "").onSuccess { repos ->
                _uiState.update { it.copy(isError = false, data = repos, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }
}

data class UserProfilerUIState(
    override val isError: Boolean = false,
    override val isLoading: Boolean = false,
    val data: List<Repository> = listOf()
) : CommonUIState(isLoading, isError)