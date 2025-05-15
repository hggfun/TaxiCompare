package com.example.taxicompare.kicksharing

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.taxicompare.R
import com.example.taxicompare.api.GetCars
import com.example.taxicompare.api.GetScooters
import com.example.taxicompare.model.ExtendedTripInfo
import com.example.taxicompare.model.UserRequest
import com.example.taxicompare.testingdata.MakeStaticKickharingPrice
import com.example.taxicompare.tripdetail.DepartureArrival
import com.example.taxicompare.tripdetail.PricePredictionChart
import com.example.taxicompare.tripdetail.TripViewModel
import com.example.taxicompare.tripdetail.isBetterPricePredicted
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun KicksharingScreen(
    innerPadding: PaddingValues,
    viewModel: TripViewModel? = null,
) {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf<MapView?>(null) }

    AndroidView(
        factory = { MapView(it)},
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        mapView.value = it
    }

    LaunchedEffect(key1 = "loadMapView") {
        snapshotFlow { mapView.value }.collect {
            it?.let {
                MapKitFactory.getInstance().onStart()
                it.onStart()
            }
        }
    }

    val staticPrice = MakeStaticKickharingPrice()
    if (mapView.value != null)
        LocateScootersOnMap(staticPrice, mapView.value!!, context)

    if (viewModel != null) {
        MakeByDistanceState(
            price = staticPrice,
            mult = 1.3,
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun LocateScootersOnMap(
    price: Int,
    mapView: MapView,
    contex: Context
) {
    val map = mapView.mapWindow.map
    var selectedMapObject by remember { mutableStateOf(false) }

    val mapObjectTapListener = MapObjectTapListener { mapObject, _ ->
        selectedMapObject = true
        true
    }

    map.move(
        CameraPosition(
            Point(55.756776, 37.523334),
            19.0f,
            150.0f,
            0.0f
        )
    )

    GetScooters().forEach { point ->
        var isTapped = remember { mutableStateOf(false) }
        map.mapObjects.addPlacemark().apply {
            geometry = Point(point.latitude, point.longitude)
            setIcon(ImageProvider.fromResource(contex, R.drawable.scooter))
            addTapListener(mapObjectTapListener)
        }
    }

    selectedMapObject.let {
        if (selectedMapObject) {
            ScooterDetailsCard(
                price,
                selectedMapObject,
                onDismissRequest = { selectedMapObject = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScooterDetailsCard(
    price: Int,
    showSheet: Boolean,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.kicksharing),
                        contentDescription = "scooter",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Urent", style = MaterialTheme.typography.titleMedium)
                        Text("Цена: от ${price}₽/минута", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

    }
}

@Composable
fun MakeByDistanceState(
    price: Int,
    mult: Double = 1.0,
    viewModel: TripViewModel,
    modifier: Modifier
) {
    var tripInfo by remember { mutableStateOf(viewModel.extendedTripInfo) }
    var request by remember { mutableStateOf(viewModel.request) }

    Column (
        modifier = modifier
    ) {
        request?.let { DepartureArrival(it.departure.name, it.arrival.name) }
        tripInfo?.let {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
            ) {
                val time = (it.duration*mult/60).toInt()
                val tripPrice = time*price
                Text(
                    text = "Время в пути ${time} мин.",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                Text(
                    text = "Примерная цена: от ${tripPrice}₽",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 16.dp)
                )
            }
        }
    }
}
