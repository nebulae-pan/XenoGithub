package io.nebula.xenogithub.ui.navigation

/**
 * Created by nebula on 2025/3/6
 */
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute

@Composable
fun AppNavigationRail(
    currentDestination: NavDestination?,
    navigateToTopLevelDestination: (NavigatorItemInfo) -> Unit,
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TOP_LEVEL_DESTINATIONS.forEach { dest ->
                NavigationRailItem(
                    selected = currentDestination.hasRoute(dest),
                    onClick = { navigateToTopLevelDestination(dest) },
                    label = {
                        Text(text = stringResource(dest.textId))
                    },
                    icon = {
                        Icon(
                            imageVector = dest.selectedIcon,
                            contentDescription = stringResource(id = dest.textId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavigationBar(
    currentDestination: NavDestination?,
    navigateToTopLevelDestination: (NavigatorItemInfo) -> Unit
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TOP_LEVEL_DESTINATIONS.forEach { dest ->
            NavigationBarItem(
                selected = currentDestination.hasRoute(dest),
                onClick = { navigateToTopLevelDestination(dest) },
                label = {
                    Text(text = stringResource(dest.textId))
                },
                icon = {
                    Icon(
                        imageVector = dest.selectedIcon,
                        contentDescription = stringResource(id = dest.textId)
                    )
                }
            )
        }
    }
}

fun NavDestination?.hasRoute(destination: NavigatorItemInfo): Boolean =
    this?.hasRoute(destination.route::class) ?: false