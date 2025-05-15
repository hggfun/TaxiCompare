package com.example.taxicompare.navigation
import android.media.Image
import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.taxicompare.cache.AppDatabase
import com.example.taxicompare.cache.PricePredictionRepository
import com.example.taxicompare.cache.TripsRepository
import com.example.taxicompare.carsharing.CarsharingScreen
import com.example.taxicompare.home.SettingsScreen
import com.example.taxicompare.tripdetail.TripViewModel
import com.example.taxicompare.tripdetail.TripViewModelFactory
import com.example.taxicompare.R
import io.ktor.client.request.request

@Composable
fun NavigationControllerSetup(appDatabase: AppDatabase) {
    val navController = rememberNavController()

    val pricePredictionRepository = PricePredictionRepository(appDatabase.pricePredictionDao())
    val tripsRepository = TripsRepository(appDatabase.tripsDao())
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(
            navController,
            pricePredictionRepository,
            tripsRepository
        )
    )

    NavHost(navController = navController, startDestination = "location_entry") {
        composable("location_entry") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToTripDetails = { departure, arrival, request ->
                    viewModel.setUserRequest(request)
                    navController.navigate("trip_details/$departure/$arrival")
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
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

            TripDetailScreen(request = viewModel.request!!, viewModel = viewModel)
        }

        composable("settings") {
            Log.v("Bober settings", "navigating settings screen")
            SettingsScreen()
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: Int) {
    object Taxi : BottomNavItem("Такси", R.drawable.baseline_dashboard_24)
    object CarSharing : BottomNavItem("Каршеринг", R.drawable.baseline_directions_car_24)
    object KickSharing : BottomNavItem("Самокаты", R.drawable.baseline_electric_scooter_24)
}

@Composable
fun MakeBottomNavigation(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    viewModel: TripViewModel
) {
    val navItems = listOf(BottomNavItem.Taxi, BottomNavItem.CarSharing, BottomNavItem.KickSharing)
    NavigationBar(
        modifier = Modifier
            .padding(0.dp)
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(item.icon), contentDescription = item.title) },
                label = { Text(item.title) },
                selected = ( viewModel.selectedNavItem.value == item),
                onClick = { onItemSelected(item) }
            )
        }
    }
}

