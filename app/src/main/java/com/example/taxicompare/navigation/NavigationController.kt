package com.example.taxicompare.navigation
import com.example.taxicompare.home.HomeScreen
import com.example.taxicompare.tripdetail.TripDetailScreen

import androidx.compose.runtime.Composable
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
