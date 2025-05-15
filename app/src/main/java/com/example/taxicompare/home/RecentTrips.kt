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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxicompare.api.GetLastTrips
import com.example.taxicompare.cache.TripEntity
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.UserRequest
import com.example.taxicompare.model.Point
import com.example.taxicompare.tripdetail.TripViewModel


@Composable
fun HorizontalCardList(
    viewModel: TripViewModel,
    onNavigateToTripDetails: (String, String, UserRequest) -> Unit
) {
    val cardItems: List<TripEntity> = viewModel.trips.collectAsState().value.take(5)
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = "Последние поездки",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        if (cardItems.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cardItems) { item ->
                    RecentTripCard(item, onNavigateToTripDetails)
                }
            }
        } else {
            Text(
                text = "Здесь будут ваши последние пеоздки",
                modifier = Modifier.padding(16.dp)
            )
        }

    }
}

@Composable
fun RecentTripCard(
    trip: TripEntity,
    onNavigateToTripDetails: (String, String, UserRequest) -> Unit
) {
    val userRequest = UserRequest(
        location = Point(1.0, 1.0),
        departure = trip.departure,
        arrival = trip.arrival,
        tariff = 0
    )
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = { onNavigateToTripDetails(trip.departure.name, trip.arrival.name, userRequest) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = trip.departure.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider() // Optional separator line

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = trip.arrival.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
