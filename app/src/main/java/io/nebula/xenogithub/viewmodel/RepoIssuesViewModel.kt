package io.nebula.xenogithub.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.nebula.xenogithub.biz.model.Issue
import io.nebula.xenogithub.biz.model.User
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.biz.KVStorageImpl
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
class RepoIssuesViewModel(private val dispatcher: CoroutineDispatcher) : ViewModel() {
    private val TAG = "RepoIssuesViewModel"
    private val _uiState = MutableStateFlow(RepoIssuesUIState())
    val uiState: StateFlow<RepoIssuesUIState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.toString())
    }

    fun loadIssues(context: Context, owner: String, repo: String) {
        _uiState.update { it.copy(isLoading = true) }
        val token = KVStorageImpl.getAccessToken(context)
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            XenoNet.loadIssuesForRepo(token, owner, repo).onSuccess { issues ->
                _uiState.update { it.copy(isError = false, data = issues, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }

    fun raiseIssue(context: Context, owner: String, repo: String) {
        val token = KVStorageImpl.getAccessToken(context)
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            XenoNet.raiseIssue(
                token,
                owner,
                repo,
                _uiState.value.issueTitle,
                _uiState.value.issueBody
            ).onSuccess { issue ->
                _uiState.update {
                    val data = it.data.toMutableList().apply {
                        add(0, issue)
                    }
                    it.copy(isError = false, data = data, isLoading = false)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }

    fun onIssueTitleChange(title: String) {
        _uiState.update { it.copy(issueTitle = title) }
    }

    fun onIssueBodyChange(body: String) {
        _uiState.update { it.copy(issueBody = body) }
    }
}

data class RepoIssuesUIState(
    override val isError: Boolean = false,
    override val isLoading: Boolean = false,
    val issueBody: String = "",
    val issueTitle: String = "",
    val user: User? = null,
    val data: List<Issue> = listOf()
) : CommonUIState(isLoading, isError)

