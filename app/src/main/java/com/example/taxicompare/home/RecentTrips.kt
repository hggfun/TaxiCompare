package com.example.taxicompare.home

import android.R.attr.maxLines
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxicompare.api.GetLastTrips
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.UserRequest
import com.yandex.mapkit.geometry.Point


@Composable
fun HorizontalCardList(onNavigateToTripDetails: (String, String, UserRequest) -> Unit) {
    val cardItems = GetLastTrips()
    Card(
        modifier = Modifier
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = "Последние поездки",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cardItems) { item ->
                RecentTripCard(item.first, item.second, onNavigateToTripDetails)
            }
        }
    }
}

@Composable
fun RecentTripCard(
    departure: String,
    arrival: String,
    onNavigateToTripDetails: (String, String, UserRequest) -> Unit
) {
    // TODO normal user request
    val userRequest = UserRequest(
        location = Point(1.0, 1.0),
        departure = Address("test_name", Point(55.751591, 37.714939)),
        arrival = Address("test_name", Point(55.753975, 37.648425)),
        tariff = 0
    )
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = { onNavigateToTripDetails(departure, arrival, userRequest) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = departure,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider() // Optional separator line

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = arrival,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
