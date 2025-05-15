package com.example.taxicompare.api

import android.util.Log
import android.widget.Toast
import com.example.taxicompare.tripdetail.TripViewModel
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONObject

suspend fun GetPricePredict(viewModel: TripViewModel, price: Int): List<Int> {
    var predictions: List<Int>

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpRequestRetry) {
            retryIf(
                maxRetries = 5,
                { request, response -> response.status.value/100 != 2}
            )
            exponentialDelay()
        }
    }
    try {
        val response: HttpResponse = client.get("http://130.193.59.88:8080/get-price-predict") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(viewModel.extendedTripInfo))
        }
        Log.v("Bober", Json.encodeToString(viewModel.extendedTripInfo))
        val request = response.request.content.toString()
        Log.v("Bober", request)
        Log.v("Bober", response.toString())
        Log.v("Bober", response.bodyAsText())
        predictions = ParsePricePredict(response.bodyAsText())
    } catch (e: Exception) {
        Log.v("Ktor Request Error", e.localizedMessage)
        predictions = listOf(0, 20, 22, 50, 52, 53, 56, 20, 10, 0, -15, -35)
    } finally {
        client.close()
    }
    return predictions.map { it + price }
}

fun ParsePricePredict(jsonString: String): List<Int> {
    val jsonObject = JSONObject(jsonString)
    val predictions = jsonObject.getJSONArray("prediction")
    var result = mutableListOf<Int>()
    val diff = predictions.getDouble(0).toInt()
    for (index in 0 until predictions.length()) {
        result.add(predictions.getDouble(index).toInt() - diff)
    }
    return result
}
