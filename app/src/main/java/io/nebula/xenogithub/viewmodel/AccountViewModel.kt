package io.nebula.xenogithub.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.nebula.xenogithub.biz.manager.IAccountManager
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
import javax.inject.Inject

/**
 * Created by nebula on 2025/3/7
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val accountManager: IAccountManager
) : ViewModel() {
    private val TAG = "AccountViewModel"
    private val _uiState = MutableStateFlow(AccountUIState())
    val uiState: StateFlow<AccountUIState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.toString())
    }

    fun oauth(context: Context) {
        if (_uiState.value.user?.token != null) {
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        val token = KVStorageImpl.getAccessToken(context)
        val code = KVStorageImpl.getAccessCode(context)
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            if (token != "") {
                // already authed, refresh user state
                XenoNet.refresh(token).onSuccess { user ->
                    user.updateToken(token)
                    accountManager.update(user)
                    _uiState.update {
                        it.copy(
                            oauth = true,
                            isError = false,
                            user = user,
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            oauth = false,
                            isError = true,
                            user = null,
                            isLoading = false
                        )
                    }
                }
            } else if (code != "") {
                // get authtoken and auth
                XenoNet.oauth(code).onSuccess { user ->
                    KVStorageImpl.saveAccessToken(context, user.token ?: "")
                    accountManager.update(user)
                    _uiState.update {
                        it.copy(
                            oauth = true,
                            isError = false,
                            user = user,
                            isLoading = false
                        )
                    }
                }.onFailure {
                    _uiState.update { it.copy(isError = true, isLoading = false) }
                }

            } else {
                _uiState.update {
                    it.copy(oauth = false, isLoading = false)
                }
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            KVStorageImpl.saveAccessCode(context, "")
            KVStorageImpl.saveAccessToken(context, "")
            accountManager.update(null)
            _uiState.update { it.copy(oauth = false) }
        }
    }

}

data class AccountUIState(
    val oauth: Boolean = false,
    override val isError: Boolean = false,
    override val isLoading: Boolean = false,
    val user: User? = null,
) : CommonUIState(isLoading, isError)