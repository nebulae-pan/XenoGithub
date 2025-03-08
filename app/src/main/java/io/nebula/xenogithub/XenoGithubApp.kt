package io.nebula.xenogithub

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.nebula.xenogithub.ui.navigation.NavigationActions
import io.nebula.xenogithub.ui.navigation.Route
import io.nebula.xenogithub.ui.screen.HomepageScreen
import io.nebula.xenogithub.ui.screen.RepoIssuesScreen
import io.nebula.xenogithub.ui.screen.SearchScreen


/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun XenoGithubApp(
    windowSize: WindowSizeClass,
) {
    XenoNavHost(windowSize)
}

@Composable
private fun XenoNavHost(
    windowSize: WindowSizeClass,
    modifier: Modifier = Modifier,
) {

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Home,
    ) {
        composable<Route.Home> {
            HomepageScreen(navigationActions, windowSize)
        }
        composable<Route.Search> {
            SearchScreen(navigationActions)
        }
        composable<Route.RepoIssues> { naviBackStackEntry ->
            RepoIssuesScreen(route = naviBackStackEntry.toRoute(), onBack = {
                navigationActions.popBack()
            })
        }

    }
}