package com.example.taxicompare.tripdetail

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

@Composable
fun PricePredictionChart(
    predictions: List<Int>,
    currentPrice: Int,
    modifier: Modifier = Modifier.height(200.dp)
) {
    val allPrices = predictions + currentPrice
    val maxPrice = (allPrices.maxOrNull() ?: 100) + 10
    val minPrice = (allPrices.minOrNull() ?: 0) - 10
    val priceRange = (maxPrice - minPrice).takeIf { it != 0 } ?: 1

    // For readability, define padding
    val verticalPadding = 32.dp
    val horizontalPadding = 40.dp

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Draw Axes
        drawLine(
            color = Color.Gray,
            start = Offset(0f, canvasHeight),
            end = Offset(canvasWidth, canvasHeight),
            strokeWidth = 2.dp.toPx()
        )

        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, canvasHeight),
            strokeWidth = 2.dp.toPx()
        )

        // Draw horizontal grid lines and price markers (vertical axis)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.DKGRAY
            textAlign = android.graphics.Paint.Align.RIGHT
            textSize = 50f
        }

        // Calculate points positions
        val pointSpacing = canvasWidth / (predictions.size - 1).coerceAtLeast(1)
        val points = predictions.mapIndexed { index, price ->
            val x = index * pointSpacing
            val yRatio = (price - minPrice).toFloat() / priceRange
            val y = canvasHeight - (yRatio * canvasHeight)
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
        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 50f
        }

        points.forEachIndexed { index, point ->
            drawCircle(
                color = Color.White,
                radius =6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color(0xFF6200EE),
                radius = 4.dp.toPx(),
                center = point
            )

            drawContext.canvas.nativeCanvas.drawText(
                "${predictions[index]}",
                point.x,
                point.y - 10.dp.toPx(),
                labelPaint
            )
        }

        // Draw current price dotted red line
        val currentYRatio = (currentPrice - minPrice).toFloat() / priceRange
        val currentY = canvasHeight - (currentYRatio * canvasHeight)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)

        drawLine(
            color = Color.Red,
            start = Offset(0f, currentY),
            end = Offset(canvasWidth, currentY),
            strokeWidth = 2.dp.toPx(),
            pathEffect = dashEffect
        )

        drawContext.canvas.nativeCanvas.drawText(
            "Текущая: $currentPrice",
            canvasWidth - 10.dp.toPx(),
            currentY - 10f,
            textPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT }
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
                val color = if (betterPriceAhead) Color.Green else Color.Red

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
