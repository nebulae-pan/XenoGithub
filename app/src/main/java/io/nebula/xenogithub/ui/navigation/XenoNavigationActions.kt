package io.nebula.xenogithub.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import io.nebula.xenogithub.R
import kotlinx.serialization.Serializable

/**
 * Created by nebula on 2025/3/6
 */

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Repos : Route

    @Serializable
    data object Account : Route

    @Serializable
    data object Search : Route

    @Serializable
    data class RepoIssues(
        val owner: String, val repo: String,
        val canRaiseIssue: Boolean = false
    ) : Route

    @Serializable
    data object LoginLanding : Route
}

data class NavigatorItemInfo(
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val textId: Int
)

class NavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: NavigatorItemInfo) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateTo(route: Route) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun popBack() {
        navController.popBackStack()
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    NavigatorItemInfo(
        route = Route.Repos,
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Default.Home,
        textId = R.string.home
    ),
    NavigatorItemInfo(
        route = Route.Account,
        selectedIcon = Icons.Default.AccountCircle,
        unselectedIcon = Icons.Default.AccountCircle,
        textId = R.string.account
    ),
)

open class NavigationInterceptor {
    class Chain(
        val route: Route,
        private val interceptors: List<NavigationInterceptor>,
        private val index: Int
    ) {
        fun process(route: Route) {
            val interceptor = interceptors[index]
            interceptor.intercept(Chain(route, interceptors, index + 1))
        }
    }

    open fun intercept(chain: Chain) {
        chain.process(chain.route)
    }
}

private val interceptors = mutableListOf<NavigationInterceptor>()

// 注册拦截器（按优先级添加）
fun addInterceptor(interceptor: NavigationInterceptor) {
    interceptors.add(interceptor)
}