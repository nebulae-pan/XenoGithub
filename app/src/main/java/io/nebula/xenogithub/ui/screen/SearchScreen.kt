package io.nebula.xenogithub.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.nebula.xenogithub.R
import io.nebula.xenogithub.ui.components.ReposList
import io.nebula.xenogithub.ui.navigation.NavigationActions
import io.nebula.xenogithub.viewmodel.SearchViewModel

/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun SearchScreen(
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                            .clickable { navigationActions.popBack() }
                            .padding(12.dp)
                    )
                    Text(
                        text = stringResource(R.string.search_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            }
            val keyboard = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                value = viewModel.searchContent,
                onValueChange = { viewModel.onUpdateSearchContent(it) },
                modifier = modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.homepage_search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.startSearch()
                    keyboard?.hide()
                }),
                singleLine = true
            )
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(160.dp)
                ) {
                    Text(text = "${stringResource(R.string.search_language)} ${uiState.language}")
                    Spacer(Modifier.weight(1f))
                    LanguageDropdownMenu {
                        viewModel.onLanguageChange(it)
                    }
                }
            }
            ReposList(
                uiState = uiState,
                data = uiState.repos,
                onRetry = { viewModel.startSearch() },
                itemOnClick = { _, _ -> })
        }
    }
}

private val languagesOption = listOf("Kotlin", "Java", "C++", "C", "Rust", "Python")

@Composable
fun LanguageDropdownMenu(onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = "",
            Modifier.clickable { expanded = !expanded })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languagesOption.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}