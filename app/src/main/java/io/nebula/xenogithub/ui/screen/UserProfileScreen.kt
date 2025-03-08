package io.nebula.xenogithub.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.nebula.xenogithub.R
import io.nebula.xenogithub.biz.model.User
import io.nebula.xenogithub.ui.components.Avatar
import io.nebula.xenogithub.ui.components.ReposList
import io.nebula.xenogithub.viewmodel.UserProfilerViewModel

/**
 * Created by nebula on 2025/3/7
 */
@Composable
fun UserProfileScreen(
    innerPaddingValues: PaddingValues,
    user: User?,
    onSignOut: () -> Unit,
    itemOnClick: (owner: String, repo: String) -> Unit,
    viewModel: UserProfilerViewModel = viewModel()
) {
    if (user == null) {
        ErrorScreen { }
        return
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.data.isEmpty()) {
            viewModel.loadUserRepos(user)
        }
    }

    Box {
        Column {
            Column {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = innerPaddingValues.calculateTopPadding())
                ) {
                    Row {
                        Spacer(
                            Modifier
                                .width(160.dp)
                                .height(100.dp)
                        )
                        Row(
                            Modifier.padding(top = 40.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = user.username,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.weight(1f))
                            Icon(
                                Icons.AutoMirrored.Default.ExitToApp,
                                contentDescription = "sign out",
                                Modifier
                                    .padding(12.dp)
                                    .clickable {
                                        onSignOut.invoke()
                                    }
                            )
                        }
                    }
                }
                Row(Modifier.padding(top = 8.dp, bottom = 12.dp)) {
                    Spacer(Modifier.width(160.dp))
                    Text(text = "${stringResource(R.string.following)}: ${user.following}")
                    Spacer(Modifier.width(16.dp))
                    Text(text = "${stringResource(R.string.followers)}: ${user.followers}")
                }
            }


            ReposList(uiState = uiState, data = uiState.data, onRetry = {
                viewModel.loadUserRepos(user)
            }, itemOnClick = { owner, repo ->
                itemOnClick(owner, repo)
            })
        }

        Avatar(
            user.avatarUrl, Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = innerPaddingValues.calculateTopPadding() + 20.dp
                )
                .size(120.dp)
        )
    }
}