package com.example.taxicompare.navigation
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import com.example.taxicompare.home.HomeScreen
import com.example.taxicompare.tripdetail.TripDetailScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavigationControllerSetup() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "location_entry") {
        composable("location_entry") {
            HomeScreen { departure, arrival ->
                navController.navigate("trip_details/$departure/$arrival")
            }
        }

        composable(
            route = "trip_details/{departure}/{arrival}",
            arguments = listOf(
                navArgument("departure") { type = NavType.StringType },
                navArgument("arrival") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val departure = backStackEntry.arguments?.getString("departure") ?: ""
            val arrival = backStackEntry.arguments?.getString("arrival") ?: ""
            TripDetailScreen(departure = departure, arrival = arrival)
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Taxi : BottomNavItem("Taxi", Icons.Default.ShoppingCart)
    object CarSharing : BottomNavItem("Carsharing", Icons.Default.ShoppingCart)
    object KickSharing : BottomNavItem("Kicksharing", Icons.Default.ShoppingCart)
}

@Composable
fun MakeBottomNavigation(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    val navItems = listOf(BottomNavItem.Taxi, BottomNavItem.CarSharing, BottomNavItem.KickSharing)
    NavigationBar(
        modifier = Modifier.padding(0.dp)
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == item,
                onClick = { onItemSelected(item) }
            )
        }
    }
}

