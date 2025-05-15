package com.example.taxicompare.api

import android.util.Log
import com.example.taxicompare.R
import com.example.taxicompare.model.ExtendedTripInfo
import com.example.taxicompare.model.TaxiConfig
import com.example.taxicompare.model.TripOffer
import com.example.taxicompare.testingdata.MakeStaticOffers
import com.example.taxicompare.tripdetail.TripViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.round

@Serializable
data class ResponseData(
    val success: Boolean,
    val price: String,
    val universalDialog: String? // Nullable since it might be null
)

fun GetOffers2(configs: List<TaxiConfig>, viewModel: TripViewModel): List<TripOffer> {
    val sampleTripOffers = listOf(
        TripOffer(iconResId = R.drawable.yandex, companyName = "Yandex", price = "209", tripTime = "15"),
        TripOffer(iconResId = R.drawable.citimobil, companyName = "Ситимобил", price = "180", tripTime = "12"),
        TripOffer(iconResId = R.drawable.taksovichkof, companyName = "Таксовичкоф", price = "300", tripTime = "20"),
        TripOffer(iconResId = R.drawable.maxim, companyName = "Maxim", price = "300", tripTime = "15"),
        TripOffer(iconResId = R.drawable.omega, companyName = "Omega", price = "189", tripTime = "12")
    )
    return sampleTripOffers
}

suspend fun GetOffers(configs: List<TaxiConfig>, viewModel: TripViewModel): List<TripOffer> = coroutineScope {
    val dynamicOffers = configs.map { config ->
        async {
            try {
                GetOneOffer(config, viewModel)
            } catch (e: Exception) {
                Log.v("Bober Basic Error", e.toString())
                null
            }
        }
    }.awaitAll().filterNotNull()

    val staticOffers: List<TripOffer> = if (dynamicOffers.isNotEmpty()) {
        val priceInt = dynamicOffers
            .mapNotNull { it.price.toDoubleOrNull() }
            .average()
            .takeIf { !it.isNaN() }
            ?.toInt() ?: 200
        MakeStaticOffers(priceInt)
    } else {
        emptyList()
    }

    return@coroutineScope dynamicOffers + staticOffers
}

suspend fun GetOneOffer(config: TaxiConfig, viewModel: TripViewModel): TripOffer {
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
        Log.v("Bober response", "Making a request")
        val response = client.request(config.url) {
            if (config.method == "get") {
                method = HttpMethod.Get
            } else {
                method = HttpMethod.Post
            }
            headers {
                config.headers.forEach {
                    append(it.name, it.value)
                }
            }
            contentType(ContentType.Application.Json)
            if (config.body != "") {
                setBody(config.body)
            }
        }
        Log.v("Bober new", "ready to get response.bodyAsText()")
        Log.v("Bober new", response.request.headers.toString())
        Log.v("Bober new", response.request.content.toString())
        Log.v("Bober new", response.bodyAsText())

        val responseBody = response.bodyAsText()
        Log.v("Bober response", response.toString())
        Log.v("Bober response body", responseBody)

        return ParseOffers(responseBody, config, viewModel)
    } finally {
        client.close()
    }
}

fun GetIconByName(name: String): Int {
    return when (name.trim().lowercase()) {
        "яндекс" -> R.drawable.yandex
        "ситимобил" -> R.drawable.citimobil
        "таксовичкофф" -> R.drawable.taksovichkof
        "maxim" -> R.drawable.maxim
        "omega" -> R.drawable.omega
        else -> R.drawable.taxi
    }
}

fun ParseOffers(jsonString: String, config: TaxiConfig, viewModel: TripViewModel): TripOffer {
    val root = JSONObject(jsonString)

    val priceAny = getJsonValueByPath(root, config.pricePath)
    val price = round(priceAny.toDouble()).toInt().toString()

    val waitAny = if (config.waitTimePath.isNotEmpty()) getJsonValueByPath(root, config.waitTimePath) else null
    val wait = when (waitAny) {
        is String -> round(waitAny.toDouble()/60).toInt().toString()
        else -> null
    }

    if (config.name == "Яндекс") {
        FillExtendedInfo(root, config, viewModel)
    }

    return TripOffer(
        iconResId = GetIconByName(config.name),
        companyName = config.name,
        price = price,
        tripTime = wait
    )
}

fun getJsonValueByPath(jsonObject: JSONObject, path: String): String {
    if (path.isEmpty()) return jsonObject.toString()
    var root: Any = jsonObject
    var result: String = ""
    val parts = path.split('/')
    for (part in parts) {
        Log.v("Bober parsing", "part " + part.toString() + " root " + root.toString())
        if (part.startsWith('[')) {
            if (root is JSONObject) {
                root = root.getJSONArray(part.substring(1, part.length - 1))
            }
            if (root is JSONArray && part.toIntOrNull() != null) {
                root = root.getJSONArray(part.substring(1, part.length - 1).toInt())
            }
        } else if (part.startsWith('!')) {
            if (root is JSONObject) {
                result = root.getString(part.substring(1, part.length))
            }
            if (root is JSONArray && part.toIntOrNull() != null) {
                result = root.getString(part.substring(1, part.length).toInt())
            }
        } else {
            if (root is JSONObject) {
                root = root.getJSONObject(part)
            }
            if (root is JSONArray && part.toIntOrNull() != null) {
                root = root.getJSONObject(part.toInt())
            }
        }
    }
    Log.v("Bober parsing result", root.toString())
    return result
}

fun FillExtendedInfo(jsonObject: JSONObject, config: TaxiConfig, viewModel: TripViewModel) {
    val departure = viewModel.request!!.departure.point
    val arrival = viewModel.request!!.arrival.point
    val timestamp = System.currentTimeMillis()/1000
    val distance = round(jsonObject.getDouble("distance")).toInt()
    val duration = round(jsonObject.getDouble("time")).toInt()

    viewModel.updateExtendedTripInfo(
        ExtendedTripInfo(
            departure.longitude,
            departure.latitude,
            arrival.longitude,
            arrival.latitude,
            timestamp,
            distance,
            duration
        )
    )
}
