package com.example.taxicompare.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxicompare.R
import com.example.taxicompare.carsharing.CarsharingScreen
import com.example.taxicompare.kicksharing.KicksharingScreen
import com.example.taxicompare.model.UserRequest
import com.example.taxicompare.navigation.BottomNavItem
import com.example.taxicompare.navigation.MakeBottomNavigation
import com.example.taxicompare.tripdetail.TripViewModel
import com.example.taxicompare.ui.theme.TaxiCompareTheme
import com.yandex.mapkit.MapKitFactory


@Composable
fun HomeScreen(
    viewModel: TripViewModel,
    onNavigateToTripDetails: (String, String, UserRequest) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Taxi) }
    viewModel.loadTrips()

    TaxiCompareTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { MakeBottomNavigation(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            ) }
        ) { innerPadding ->
            when (selectedItem) {
                BottomNavItem.Taxi -> TaxiScreen(innerPadding, viewModel, onNavigateToTripDetails, onNavigateToSettings)
                BottomNavItem.CarSharing -> CarsharingScreen()
                BottomNavItem.KickSharing -> KicksharingScreen()
            }
        }
    }
}

@Composable
fun TaxiScreen(
    innerPadding: PaddingValues,
    viewModel: TripViewModel,
    onNavigateToTripDetails: (String, String, UserRequest) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsRow(onNavigateToSettings)
        AnimatedCardWithBottomSheet(viewModel, onNavigateToTripDetails)
        HorizontalCardList(viewModel, onNavigateToTripDetails)
        AdvertisementWidget()
    }
}

@Composable
fun SettingsRow(
    onNavigateToSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Лучшие\nцены на такси",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )

        IconButton(
            onClick = {
                Log.v("Bober settings", "button settings screen")
                onNavigateToSettings()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }
    }
}

@Composable
fun LocationInputScreen(modifier: Modifier = Modifier) {
    // State variables to hold user input
    var departureLocation by remember { mutableStateOf("") }
    var arrivalLocation by remember { mutableStateOf("") }
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        TextField(
            value = departureLocation,
            onValueChange = { departureLocation = it },
            label = { Text("Departure Location") },
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Search,
//                            contentDescription = "Search Icon"
//                        )
//                    },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = arrivalLocation,
            onValueChange = { arrivalLocation = it },
            label = { Text("Arrival Location") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}
