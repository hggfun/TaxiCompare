package com.example.taxicompare.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.taxicompare.ui.theme.TaxiCompareTheme


@Composable
fun HomeScreen(onNavigateToTripDetails: (String, String) -> Unit) {
    TaxiCompareTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                SettingsRow()
//                LocationInputScreen(
//                    modifier = Modifier.padding(innerPadding)
//                )
                AnimatedCardWithBottomSheet(onNavigateToTripDetails)
                HorizontalCardList(onNavigateToTripDetails)
                AdvertisementWidget()
            }
        }
    }
}

@Composable
fun SettingsRow() {
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
                .padding(start = 8.dp) // Add some spacing between the icon and text
        )

        IconButton(
            onClick = {
                // Handle the gear button click action
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
        modifier = Modifier.fillMaxWidth().padding(12.dp)
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
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = arrivalLocation,
            onValueChange = { arrivalLocation = it },
            label = { Text("Arrival Location") },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}
