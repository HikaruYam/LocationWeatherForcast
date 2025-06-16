package com.example.locationweatherforcast.navigation

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val accessibilityLabel: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = NavigationRoutes.CURRENT_WEATHER,
        icon = Icons.Default.LocationOn,
        label = "天気",
        accessibilityLabel = "明日の天気予報画面に移動"
    ),
    BottomNavItem(
        route = NavigationRoutes.FAVORITE_LOCATIONS,
        icon = Icons.Default.Favorite,
        label = "お気に入り",
        accessibilityLabel = "お気に入りの場所画面に移動"
    )
)

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.semantics {
            contentDescription = "ナビゲーションバー"
        }
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            val selectionState = if (isSelected) "選択中" else "未選択"
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null, // Description handled by the item
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to start destination to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = "${item.accessibilityLabel}, $selectionState"
                }
            )
        }
    }
}