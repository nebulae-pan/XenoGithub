package io.nebula.xenogithub.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.nebula.xenogithub.R
import io.nebula.xenogithub.ui.components.IssueItem
import io.nebula.xenogithub.ui.navigation.Route
import io.nebula.xenogithub.viewmodel.RepoIssuesUIState
import io.nebula.xenogithub.viewmodel.RepoIssuesViewModel

/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun RepoIssuesScreen(
    route: Route.RepoIssues,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: RepoIssuesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadIssues(context, route.owner, route.repo)
    }

    var openIssueInputDialog by rememberSaveable { mutableStateOf(false) }
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary) {
                Row(
                    Modifier
                        .padding(top = padding.calculateTopPadding())
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack, contentDescription = "back",
                        modifier = Modifier
                            .clickable { onBack.invoke() }
                            .padding(12.dp)
                    )
                    Text(
                        text = stringResource(R.string.details),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            }
            Text(
                text = route.repo,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 36.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(24.dp)
            )

            Row {
                Text(
                    text = stringResource(R.string.issues),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(12.dp)
                )
                Spacer(Modifier.weight(1f))
                if (route.canRaiseIssue) {
                    Button(
                        onClick = { openIssueInputDialog = !openIssueInputDialog },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.raise),
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.Center)
                        )
                    }

                    uiState.isError -> {
                        ErrorScreen {
                        }
                    }

                    uiState.data.isEmpty() -> {
                        EmptyScreen()
                    }

                    else -> {
                        Column {

                            LazyColumn(
                                modifier = modifier,
                                contentPadding = PaddingValues(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(items = uiState.data,
                                    key = { it.id }) { issue ->
                                    IssueItem(issue)
                                }
                            }
                        }
                    }
                }
                if (openIssueInputDialog) {
                    IssueInputDialog(viewModel, uiState, onDismissRequest = {
                        openIssueInputDialog = false
                    }, onConfirmation = {
                        // Only supports raising issues to oneself
                        viewModel.raiseIssue(context, route.owner, route.repo)
                        openIssueInputDialog = false
                    })
                }
            }
        }
    }
}

@Composable
fun IssueInputDialog(
    viewModel: RepoIssuesViewModel,
    uiState: RepoIssuesUIState,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = {
            TextField(
                value = uiState.issueTitle,
                onValueChange = { viewModel.onIssueTitleChange(it) },
                label = { Text(text = stringResource(R.string.issues_title)) })
        },
        text = {
            TextField(
                value = uiState.issueBody,
                onValueChange = { viewModel.onIssueBodyChange(it) },
                label = { Text(text = stringResource(R.string.issues_body)) })
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("raise")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("cancel")
            }
        }
    )
}

@Composable
@Preview
private fun Preview() {
//    RepoIssuesScreen(Route.RepoIssues("", "Amazing Repo"), onBack = {})
}