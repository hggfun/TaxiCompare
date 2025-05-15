package com.example.taxicompare.tripdetail

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.taxicompare.R
import com.example.taxicompare.api.GetPricePredict
import com.example.taxicompare.cache.PricePredictionDao
import com.example.taxicompare.cache.PricePredictionEntity
import com.example.taxicompare.cache.PricePredictionRepository
import com.example.taxicompare.ui.App
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun test3() {
    Column {
        Card(
            Modifier
                .padding(horizontal = 16.dp, vertical = 48.dp)
                .wrapContentHeight()
        ) {
            PricePredictionChart(
                listOf(500, 510, 520, 500),
                500
            )
        }
        PricePredictionChart(
            listOf(0, 20, 22, 50, 52, 53, 56, 20, 10, 0, -15, -35).map { a -> a+500 },
            500
        )
    }

}

@Composable
fun PricePredictionChart(
    predictions: List<Int>,
    currentPrice: Int,
    modifier: Modifier = Modifier.height(220.dp)
) {
    val allPrices = predictions + currentPrice
    val maxPrice = (allPrices.maxOrNull() ?: 100) + 50
    val minPrice = (allPrices.minOrNull() ?: 0) - 50
    val priceRange = (maxPrice - minPrice).takeIf { it != 0 } ?: 1
    var previousNoted = -10

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(16.dp)
    ) {
        // distance from borders of Canvas
        val offset = 16.dp.toPx()
        // chart real sizes
        val chartWidth = size.width - offset * 2
        val chartHeight = size.height - offset
        // distance between Points on chart
        val pointSpacing = chartWidth / (predictions.size - 1).coerceAtLeast(1)

        val now = LocalDateTime.now()
        val times = List(13) { index ->
            now.plusMinutes(5L * index)
        }
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 48f
        }

        for (i in 1 until 6) {
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, chartHeight / 6 * i),
                end = Offset(chartWidth + offset * 2, chartHeight / 6 * i),
                strokeWidth = (0.5).dp.toPx()
            )
        }


//        drawLine(
//            color = Color.Gray,
//            start = Offset(0f, 0f),
//            end = Offset(0f, chartHeight),
//            strokeWidth = 2.dp.toPx()
//        )


        // Calculate points positions
        val points = predictions.mapIndexed { index, price ->
            val yRatio = (maxPrice - price).toFloat() / priceRange
            val x = offset + index * pointSpacing
            val y = chartHeight * yRatio
            Offset(x, y)
        }

        // Draw prediction line
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color.Gray,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw circles and price labels on each point
        points.forEachIndexed { index, point ->
            Log.v("Bober chart2", "index: $index")
            Log.v("Bober chart", "index:$index, current:${predictions[index]}, min:$minPrice, max:$maxPrice, point:$point")
            if (
                previousNoted + 2 < index &&
                (
                        index == 0 ||
                                predictions[index] - 50 == minPrice ||
                                predictions[index] + 50 == maxPrice
                        )
            ) {
                previousNoted = index
                drawCircle(
                    color = Color.White,
                    radius =6.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.Black,
                    radius = 4.dp.toPx(),
                    center = point
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "${predictions[index]}",
                    point.x,
                    point.y - 10.dp.toPx(),
                    labelPaint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    times[index].format(timeFormatter),
                    point.x,
                    chartHeight + offset,
                    labelPaint
                )
            }
            drawLine(
                color = Color.LightGray,
                start = Offset(points[index].x, 0f),
                end = Offset(points[index].x, chartHeight),
                strokeWidth = (0.5).dp.toPx()
            )
        }

        // Draw current price dotted red line
        val currentYRatio = (maxPrice - currentPrice).toFloat() / priceRange
        val currentY = chartHeight * currentYRatio
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)

        drawLine(
            color = Color.Red,
            start = Offset(0f, currentY),
            end = Offset(chartWidth + offset * 2, currentY),
            strokeWidth = 2.dp.toPx(),
            pathEffect = dashEffect
        )
    }
}

fun isBetterPricePredicted(predictions: List<Int>, currentPrice: Int): Boolean {
    return predictions.any { it < currentPrice }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricePredictionCard(
    iconResId: Int, // Resource ID for the image/icon
    companyName: String,
    price: Int,
    tripTime: String?,
    viewModel: TripViewModel,
    showSheet: Boolean,
    onDismissRequest: () -> Unit
) {
    var prices by remember { mutableStateOf<List<Int>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var adWatched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(true) {
        try {
            viewModel.loadPredictions(price)
        } catch (e: Exception) {
            errorMessage = "Failed to fetch prices: ${e.localizedMessage}"
        }
    }
    prices = viewModel.prices

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
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
                            Text("Цена: $price₽", style = MaterialTheme.typography.bodyMedium)
                            if (!tripTime.isNullOrEmpty()) {
                                Text("Время подачи: $tripTime минут", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                val betterPriceAhead = isBetterPricePredicted(prices, price)
                val color = if (betterPriceAhead) Color(0xFF4CAF50) else Color.Red

                if (adWatched) {
                    val text = if (betterPriceAhead) "Можно подождать и цена будет ниже"
                    else "Не стоит ждать, цена только вырстет"
                    MakeOutlinedCard(text, color) {}
                    PricePredictionChart(prices, price)
                } else {
                    val app = LocalContext.current.applicationContext as App
                    MakeOutlinedCard(
                        "Просмотрите рекламу для получения прогноза цены",
                        MaterialTheme.typography.bodyMedium.color
                    ) { app.showAd{ adWatched = true } }
                }
            }
        }

    }
}

@Composable
fun MakeOutlinedCard(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    OutlinedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, color),
        onClick = onClick,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
