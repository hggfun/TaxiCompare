package com.example.taxicompare.api

import com.example.taxicompare.model.Address

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taxicompare.BuildConfig
import com.example.taxicompare.model.Point
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.lang.Thread.sleep
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.min

@Preview
@Composable
fun vkMapTest() {
    LocationRequestScreen()
    // Шелепихинское шоссе 3
}

@Composable
fun LocationRequestScreen() {
    var locationName by remember { mutableStateOf("Test1") }
    var resultTexts by remember { mutableStateOf(emptyList<Address>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = locationName,
            onValueChange = { locationName = it },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { resultTexts = Request(locationName) }
        ) {
            Text("Get Address")
        }
        LazyColumn {
            items(resultTexts) { address ->
                Text(
                    text = address.name + " " + address.point.longitude + " " + address.point.latitude,
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}


fun Request(placeName: String): List<Address> {
    val client = HttpClient(CIO) {
    }
    val encodedAddress = URLEncoder.encode(placeName, StandardCharsets.UTF_8.toString())
    val url = "https://demo.maps.vk.com/api/suggest?api_key=test&q=$encodedAddress&fields=ref,+name,+address"
    val value = runBlocking {
        val response = client.get(url)

        val jsonObject = JSONObject(response.bodyAsText())
        val resultsArray = jsonObject.getJSONArray("results")

        return@runBlocking resultsArray
    }

    var result = arrayListOf<Address>()
    for (i in 0 until min(4, value.length())) {
        Log.v("bober tag", value.getJSONObject(i).toString())
        result.add(MakeSearch(value.getJSONObject(i).getString("ref")))
    }

    return result
}

fun MakeSearch(ref: String): Address {
    val client = HttpClient(CIO) {
    }
    val url = "https://demo.maps.vk.com/api/search?api_key=test&q=$ref&fields=address,+pin"
    val value = runBlocking {
        sleep(1000)
        val response = client.get(url)

        val jsonObject = JSONObject(response.bodyAsText())

        Log.v("bober tag request", jsonObject.toString())
        return@runBlocking jsonObject.getJSONArray("results").getJSONObject(0)
    }

    Log.v("bober tag 2", value.toString())
    val pin = value.getJSONArray("pin")
    var result = Address(
        name = value.getString("address"),
        description = "description",
        point = Point(pin.getDouble(0), pin.getDouble(1))
    )
    return result
}

suspend fun Request2(placeName: String): List<Address> {
    val encodedAddress = URLEncoder.encode(placeName, StandardCharsets.UTF_8.toString())
    val client = HttpClient(CIO) {}
    val url = "https://geocode-maps.yandex.ru/v1/?apikey=${BuildConfig.GEOCODER_API_KEY}&geocode=${encodedAddress}&format=json\n"
    val response = client.get(url)

    val jsonObject = JSONObject(response.bodyAsText())
    return ParseGeocoderAddresses(jsonObject)
}

fun ParseGeocoderAddresses(jsonObject: JSONObject): List<Address> {
    val jsonAddresses = jsonObject.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember")
    Log.v("Bober ya geocoder parse", jsonAddresses.toString())
    var addresses = arrayListOf<Address>()
    for (index in 0 until jsonAddresses.length()) {
        val jsonAddress = jsonAddresses.getJSONObject(index).getJSONObject("GeoObject")
        val jsonPoint = jsonAddress.getJSONObject("Point")
        val (latStr, longStr) = jsonPoint.getString("pos").split(" ")

        val address = Address(
            name = jsonAddress.getString("name"),
            description = jsonAddress.getString("description"),
            point = Point(latStr.toDouble(), longStr.toDouble())
        )
        addresses.add(address)
    }
    return addresses
}