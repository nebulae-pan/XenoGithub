package io.nebula.xenogithub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.nebula.xenogithub.biz.manager.IAccountManager
import io.nebula.xenogithub.biz.model.Repository
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.ui.utils.CommonUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by nebula on 2025/3/7
 */
@HiltViewModel
class ReposViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val accountManager: IAccountManager
) : ViewModel() {
    private val TAG = "ReposViewModel"
    private val _uiState = MutableStateFlow(ReposUIState())
    val uiState: StateFlow<ReposUIState> = _uiState
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.toString())
    }

    fun loadRepos() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            XenoNet.retrieveTrendingRepos()
                .onSuccess { repos ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            data = repos
                        )
                    }
                }.onFailure { e ->
                    Log.e(TAG, e.toString())
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }
        }
    }

    fun isSigned(): Boolean {
        return accountManager.currentAccount() != null
    }
}

data class ReposUIState(
    override val isError: Boolean = false,
    override val isLoading: Boolean = false,
    val data: List<Repository> = listOf()
) : CommonUIState(isLoading, isError)