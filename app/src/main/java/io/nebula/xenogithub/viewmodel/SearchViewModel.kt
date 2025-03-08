package io.nebula.xenogithub.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.nebula.xenogithub.biz.model.Repository
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
 * Created by nebula on 2025/3/6
 */
class SearchViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {
    private val TAG = "SearchViewModel"
    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState: StateFlow<SearchUIState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.toString())
    }

    var searchContent by mutableStateOf("")
        private set

    fun onLanguageChange(language: String) {
        _uiState.update { it.copy(language = language) }
    }

    fun onUpdateSearchContent(content: String) {
        searchContent = content
    }

    fun startSearch() {
        if (searchContent == "") {
            return
        }
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            _uiState.update { it.copy(isLoading = true) }
            XenoNet.reposCommonSearch(
                query = searchContent,
                language = _uiState.value.language,
            ).onSuccess { repos ->
                _uiState.update { it.copy(isError = false, repos = repos, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }
}

data class SearchUIState(
    val language: String = "Kotlin",
    override val isError: Boolean = false,
    override val isLoading: Boolean = false,
    val repos: List<Repository> = listOf()
) : CommonUIState(isLoading, isError)