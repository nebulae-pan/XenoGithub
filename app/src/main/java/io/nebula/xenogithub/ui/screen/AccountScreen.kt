package io.nebula.xenogithub.ui.screen

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import io.nebula.xenogithub.biz.network.service.AUTH_URL
import io.nebula.xenogithub.ui.navigation.NavigationActions
import io.nebula.xenogithub.ui.navigation.Route
import io.nebula.xenogithub.viewmodel.AccountViewModel

/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun AccountScreen(
    navigationActions: NavigationActions,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.oauth(context)
    }
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.Center)
                )
            }

            uiState.oauth -> {
                UserProfileScreen(innerPadding, uiState.user, onSignOut = {
                    viewModel.signOut(context)
                }, itemOnClick = { owner, repo ->
                    navigationActions.navigateTo(Route.RepoIssues(owner, repo, true))
                })
            }

            else -> {
                AnonymousScreen {
                    CustomTabsIntent.Builder().build().launchUrl(
                        context, Uri.parse(AUTH_URL)
                    )
                }
            }
        }
    }
}

@Composable
fun AnonymousScreen(onLogin: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { onLogin.invoke() }
        ) {
            Text(stringResource(R.string.homepage_sign_in))
        }
    }
}