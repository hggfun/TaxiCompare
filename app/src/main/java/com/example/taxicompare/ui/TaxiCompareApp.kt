package com.example.taxicompare.ui

import androidx.compose.foundation.Image
import com.example.taxicompare.ui.theme.TaxiCompareTheme
import com.example.taxicompare.R


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun TaxiCompareApp() {
    TaxiCompareTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(4.dp)) {
                SettingsRow()
                LocationInputScreen(
                    modifier = Modifier.padding(innerPadding)
                )
                HorizontalCardList()
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

    // Column to arrange elements vertically
    Column(modifier = modifier.padding(12.dp)) {
        // Text field for departure location
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
                ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = arrivalLocation,
                    onValueChange = { arrivalLocation = it },
                    label = { Text("Arrival Location") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HorizontalCardList() {
    // A sample list of data to display
    val cardItems = listOf("Card 1", "Card 2", "Card 3", "Card 4")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cardItems) { item ->
            TripCard(R.drawable.adv8, item, price = "158 рублей", tripTime = "12o hours")
        }
    }
}



@Composable
fun TripCard(
    iconResId: Int, // Resource ID for the image/icon
    companyName: String,
    price: String,
    tripTime: String
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                //verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "Company Icon",
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = companyName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f) // Expand to fill the remaining space
                )

                IconButton(onClick = {
                    // Handle like action
                }) {
                    Icon(Icons.Default.ThumbUp, contentDescription = "Like")
                }

                IconButton(onClick = {
                    // Handle dislike action
                }) {
                    Icon(Icons.Default.ThumbUp, contentDescription = "Dislike")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Price: $price",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "Trip Time: $tripTime",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
