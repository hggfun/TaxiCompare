package com.example.taxicompare.tripdetail

import com.example.taxicompare.R
import com.example.taxicompare.ui.theme.TaxiCompareTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TripDetailScreen(departure: String, arrival: String) {
    TaxiCompareTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(4.dp)) {
                TripCard(R.drawable.adv8, "Yandex", "100", "100")
            }
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