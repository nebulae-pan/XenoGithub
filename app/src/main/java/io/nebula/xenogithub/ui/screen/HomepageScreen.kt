package io.nebula.xenogithub.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.nebula.xenogithub.ui.navigation.AppNavigationBar
import io.nebula.xenogithub.ui.navigation.AppNavigationRail
import io.nebula.xenogithub.ui.navigation.NavigationActions
import io.nebula.xenogithub.ui.navigation.Route

/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun HomepageScreen(
    navigationActions: NavigationActions,
    windowSize: WindowSizeClass
) {
    val homepageNavController = rememberNavController()
    val homepageNavigationActions = remember(homepageNavController) {
        NavigationActions(homepageNavController)
    }
    val navBackStackEntry by homepageNavController.currentBackStackEntryAsState()
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        currentDestination = navBackStackEntry?.destination,
                        navigateToTopLevelDestination = homepageNavigationActions::navigateTo
                    )
                }) { padding ->
                HomepageNaviHost(homepageNavController, navigationActions, padding)
            }
        }

        WindowWidthSizeClass.Medium -> {
            Row(modifier = Modifier.fillMaxWidth()) {
                AppNavigationRail(
                    currentDestination = navBackStackEntry?.destination,
                    navigateToTopLevelDestination = homepageNavigationActions::navigateTo
                )
                HomepageNaviHost(homepageNavController, navigationActions, PaddingValues())
            }
        }

        else -> {
            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        currentDestination = navBackStackEntry?.destination,
                        navigateToTopLevelDestination = homepageNavigationActions::navigateTo
                    )
                }) { padding ->
                HomepageNaviHost(homepageNavController, navigationActions, padding)
            }
        }
    }

}

@Composable
fun HomepageNaviHost(
    homepageNavController: NavHostController,
    navigationActions: NavigationActions,
    padding: PaddingValues
) {
    NavHost(
        navController = homepageNavController,
        startDestination = Route.Repos,
    ) {
        composable<Route.Repos> {
            ReposScreen(navigationActions, padding)
        }
        composable<Route.Account> {
            AccountScreen(navigationActions, padding)
        }

        // todo: landing page
//            composable<Route.LoginLanding>(
//                deepLinks = listOf(
//                    navDeepLink<Route.LoginLanding>(basePath = "xenode://auth/callback")
//                )
//            ) {
//                AccountScreen(navigationActions, padding)
//            }
    }
}
