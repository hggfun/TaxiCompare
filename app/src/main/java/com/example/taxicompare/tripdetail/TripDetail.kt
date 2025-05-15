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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Canvas
import com.example.taxicompare.api.GetOffers
import com.example.taxicompare.home.AdvertisementWidget
import com.example.taxicompare.home.AnimatedCardWithBottomSheet
import com.example.taxicompare.home.HorizontalCardList
import com.example.taxicompare.home.SettingsRow
import com.example.taxicompare.model.TripOffer

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.taxicompare.api.GetConfigs
import com.example.taxicompare.api.GetPricePredict
import com.example.taxicompare.cache.PricePredictionRepository
import com.example.taxicompare.model.UserRequest
import com.example.taxicompare.navigation.BottomNavItem
import com.example.taxicompare.testingdata.MakeStaticCarsharingPrice
import com.example.taxicompare.testingdata.MakeStaticKickharingPrice


@Composable
fun TripDetailScreen(
    request: UserRequest,
    viewModel: TripViewModel
) {
    TaxiCompareTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                DepartureArrival(request.departure.name, request.arrival.name)
                Spacer(modifier = Modifier.padding(12.dp))
                MakeTripDetailScreen(request, viewModel)
            }
        }
    }
}

@Composable
fun MakeTripDetailScreen(
    request: UserRequest,
    viewModel: TripViewModel
) {
    var tripOffers by remember { mutableStateOf(emptyList<TripOffer>()) }
    LaunchedEffect(true) {
        val configs = GetConfigs(request)
        tripOffers = GetOffers(configs, viewModel)
    }

    val minPrice = tripOffers.minOfOrNull { it.price.toInt() }
    val minTime = tripOffers
        .filter { !it.tripTime.isNullOrEmpty() }
        .minOfOrNull { it.tripTime!!.toInt() }

    if (tripOffers.isEmpty()) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Ищем лучшие предложения...",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        LazyColumn () {
            item { MakeOtherTransportSuggestions(viewModel) }
            items(tripOffers) { tripOffer ->
                val isCheapest = tripOffer.price.toInt() == minPrice
                val isFastest = tripOffer.tripTime?.toIntOrNull() == minTime

                TripCard(
                    iconResId = tripOffer.iconResId,
                    companyName = tripOffer.companyName,
                    price = tripOffer.price,
                    tripTime = tripOffer.tripTime,
                    viewModel = viewModel,
                    isCheapest = isCheapest,
                    isFastest = isFastest
                )
            }
        }
    }


}

@Composable
fun TripCard(
    iconResId: Int,
    companyName: String,
    price: String,
    tripTime: String?,
    viewModel: TripViewModel,
    isCheapest: Boolean,
    isFastest: Boolean
) {
    var showSheet by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = { showSheet = true }
    ) {
        TripCardLabels(isCheapest, isFastest)
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = companyName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(companyName, style = MaterialTheme.typography.titleMedium)
                Text("Цена: $price₽", style = MaterialTheme.typography.bodyMedium)
                if (!tripTime.isNullOrEmpty()) {
                    Text("Время подачи: $tripTime минут", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showSheet) {
        PricePredictionCard(
            iconResId,
            companyName,
            price.toInt(),
            tripTime,
            viewModel,
            showSheet,
            onDismissRequest = { showSheet = false}
        )
    }
}

@Composable
fun DepartureArrival(
    departure: String,
    arrival: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = "Куда поедем?",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = departure,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Left
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = arrival,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Left
            )
        }
    }
}

@Preview
@Composable
fun test() {
    Column(modifier = Modifier.fillMaxWidth()) {
        TripCardLabels(true, true)
        TripCardLabels(false, true)
        TripCardLabels(true, false)
        TripCardLabels(false, false)
    }
}

@Composable
fun TripCardLabels(
    isCheapest: Boolean,
    isFastest: Boolean
) {
    Row {
        if (isCheapest) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .wrapContentWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = "самый дешевый",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
        if (isFastest) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .wrapContentWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = "самый быстрый",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun MakeOtherTransportSuggestions(
    viewModel: TripViewModel
) {
    var tripInfo by remember { mutableStateOf(viewModel.extendedTripInfo) }

    tripInfo?.let {
        Row (
            Modifier.fillMaxWidth()
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(start = 16.dp, end = 4.dp, bottom = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
                onClick = {
                    viewModel.setNavItem(BottomNavItem.CarSharing)
                    viewModel.getNavController().popBackStack("location_entry", inclusive = false)
                }
            ) {
                val time = (it.duration/60).toInt()
                val tripPrice = time*MakeStaticCarsharingPrice()
                Text(
                    text = "На каршеринге\nот ${tripPrice}₽",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .padding(start = 4.dp, end = 16.dp, bottom = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
                onClick = {
                    viewModel.setNavItem(BottomNavItem.KickSharing)
                    viewModel.getNavController().popBackStack("location_entry", inclusive = false)
                }
            ) {
                val time = (it.duration*1.3/60).toInt()
                val tripPrice = time*MakeStaticKickharingPrice()
                Text(
                    text = "На самокате\nот ${tripPrice}₽",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
