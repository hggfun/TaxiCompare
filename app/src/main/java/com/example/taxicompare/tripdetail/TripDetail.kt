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
import com.example.taxicompare.api.GetConfigs
import com.example.taxicompare.api.GetPricePredict
import com.example.taxicompare.cache.PricePredictionRepository
import com.example.taxicompare.model.UserRequest

//@Preview
//@Composable
//fun Preview() {
//    TripDetailScreen("уник", "дом")
//}

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
//    val tripOffers: List<TripOffer> = GetOffers()
    LaunchedEffect(true) {
        val configs = GetConfigs(request)
        tripOffers = GetOffers(configs, viewModel)
    }
    LazyColumn {
        items(tripOffers) { tripOffer ->
            TripCard(
                iconResId = tripOffer.iconResId,
                companyName = tripOffer.companyName,
                price = tripOffer.price,
                tripTime = tripOffer.tripTime,
                viewModel
            )
        }
    }
}

@Composable
fun TripCard(
    iconResId: Int, // Resource ID for the image/icon
    companyName: String,
    price: String,
    tripTime: String?,
    viewModel: TripViewModel
) {
    var showSheet by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = { showSheet = true }
    ) {
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
                Text("Цена: $price рублей", style = MaterialTheme.typography.bodyMedium)
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

//@Composable
//fun PricePredictionChart(predictions: List<Int>, currentPrice: Int) {
//    Column(modifier = Modifier.padding(top = 12.dp)) {
//        Text(
//            text = "Price Prediction Chart",
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        Canvas(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//                .padding(16.dp)
//        ) {
//            val maxPrice = (predictions + currentPrice).maxOrNull() ?: 100
//            val minPrice = (predictions + currentPrice).minOrNull() ?: 0
//            val priceRange = maxPrice - minPrice
//            val canvasHeight = size.height
//            val canvasWidth = size.width
//
//            // Draw simple horizontal guides
//            (0..4).forEach { step ->
//                val y = canvasHeight / 4 * step
//                drawLine(
//                    color =Color.LightGray,
//                    start = Offset(0f, y),
//                    end = Offset(canvasWidth, y)
//                )
//            }
//
//            val pointSpacing = (canvasWidth / (predictions.size)).coerceAtLeast(1f)
//            val points = predictions.mapIndexed { index, price ->
//                val x = index * pointSpacing
//                val yRatio = (price - minPrice).toFloat() / priceRange
//                val y = canvasHeight - (yRatio * canvasHeight)
//                Offset(x, y)
//            }
//
//            // Draw prediction curve
//            for (i in 0 until points.size - 1) {
//                drawLine(
//                    color = Color.Blue,
//                    start = points[i],
//                    end = points[i + 1],
//                    strokeWidth = 4f,
//                )
//            }
//
//            // Mark current price line
//            val currentPriceY = canvasHeight - ((currentPrice - minPrice).toFloat() / priceRange) * canvasHeight
//            drawLine(
//                color = Color.Red,
//                strokeWidth = 1.dp.toPx(),
//                start = Offset(0f, currentPriceY),
//                end = Offset(canvasWidth, currentPriceY),
//                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
//            )
//        }
//    }
//}

