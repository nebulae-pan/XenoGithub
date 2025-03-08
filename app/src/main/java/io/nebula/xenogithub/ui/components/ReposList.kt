package io.nebula.xenogithub.ui.components

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nebula.xenogithub.biz.model.Repository
import io.nebula.xenogithub.ui.screen.EmptyScreen
import io.nebula.xenogithub.ui.screen.ErrorScreen
import io.nebula.xenogithub.ui.utils.CommonUIState

/**
 * reuse for multiple screen
 *
 * Created by nebula on 2025/3/7
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReposList(
    uiState: CommonUIState,
    modifier: Modifier = Modifier,
    data: List<Repository>,
    onRetry: () -> Unit,
    itemOnClick: (owner: String, repo: String) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        var isRefreshing by remember { mutableStateOf(false) }
        when {
            !isRefreshing && uiState.isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.Center)
                )
            }

            uiState.isError -> {
                ErrorScreen {
                    onRetry.invoke()
                }
            }

            data.isEmpty() -> {
                EmptyScreen()
            }

            else -> {
                val state = rememberPullToRefreshState()
                val onRefresh: () -> Unit = {
                    isRefreshing = true
                    onRetry.invoke()
                    isRefreshing = false
                }
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    state = state,
                    indicator = {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = isRefreshing,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            state = state
                        )
                    },
                ) {
                    val lazyListState =
                        rememberSaveable(key = "shared", saver = lazyListStateSaver) {
                            LazyListState()
                        }
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = data,
                            key = { it.id }) { repo ->
                            RepoItem(
                                repo,
                                itemOnClick = { itemOnClick(repo.owner.name, repo.name) })
                        }
                    }
                }
            }
        }
    }

}

val lazyListStateSaver = run {
    val keyIndex = "INDEX"
    val keyOffset = "OFFSET"
    Saver<LazyListState, Bundle>(
        save = { state ->
            Bundle().apply {
                putInt(keyIndex, state.firstVisibleItemIndex)
                putInt(keyOffset, state.firstVisibleItemScrollOffset)
            }
        },
        restore = { bundle ->
            LazyListState(
                firstVisibleItemIndex = bundle.getInt(keyIndex),
                firstVisibleItemScrollOffset = bundle.getInt(keyOffset)
            )
        }
    )
}