package io.nebula.xenogithub.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.nebula.xenogithub.R
import io.nebula.xenogithub.ui.components.ReposList
import io.nebula.xenogithub.ui.navigation.NavigationActions
import io.nebula.xenogithub.ui.navigation.Route
import io.nebula.xenogithub.viewmodel.ReposViewModel

/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun ReposScreen(
    navigationActions: NavigationActions,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    reposViewModel: ReposViewModel = hiltViewModel()
) {
    val uiState by reposViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.data.isEmpty()) {
            reposViewModel.loadRepos()
        }
    }
    Column {
        Surface(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary) {
            Row(
                Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.homepage_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = 48.dp)
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    Modifier
                        .clickable { navigationActions.navigateTo(Route.Search) }
                        .padding(12.dp)
                        .size(36.dp)
                )
            }
        }

        val context = LocalContext.current
        ReposList(uiState = uiState,
            data = uiState.data,
            onRetry = { reposViewModel.loadRepos() },
            itemOnClick = { owner, repo ->
                if (reposViewModel.isSigned()) {
                    navigationActions.navigateTo(Route.RepoIssues(owner, repo))
                } else {
                    Toast.makeText(context, R.string.homepage_need_sign_in, Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}