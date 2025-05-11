package com.example.taxicompare.api

import android.content.Context
import com.yandex.mapkit.geometry.Point
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.taxicompare.BuildConfig
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.RequestHeaders
import com.example.taxicompare.model.TaxiConfig
import com.example.taxicompare.model.UserRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.json.JSONArray
import org.json.JSONObject

//@Preview
//@Composable
//fun test() {
//    val userRequest = UserRequest(
//        location = Point(1.0, 1.0),
//        departure = Address("test_name", Point(55.751591, 37.714939)),
//        arrival = Address("test_name", Point(55.753975, 37.648425)),
//        tariff = 0
//    )
//    val configs = runBlocking {
//        return@runBlocking GetConfigs(userRequest)
//    }
//    for (config in configs) {
//        Log.v("TestGetOffers", config.toString())
//    }
//
//    val offers = runBlocking {
//        return@runBlocking GetOffers(configs)
//    }
//
//    for (offer in offers) {
//        Log.v("TestGetOffers", offer.toString())
//    }
//}

suspend fun GetConfigs(request: UserRequest): List<TaxiConfig> {
    var configs: JSONArray

    val client = HttpClient(CIO)
    try {
        val response: HttpResponse = client.get("http://130.193.59.88:8080/get-config") {
            contentType(ContentType.Application.Json)
            setBody("{\"type\": \"taxi\"}")
        }
        configs = JSONObject(response.bodyAsText()).getJSONArray("configs")
    } catch (e: Exception) {
        Log.v("Ktor Request Error", e.localizedMessage)
        configs = loadJSONFromAsset().getJSONArray("configs")
    } finally {
        client.close()
    }

    var taxiConfigs = arrayListOf<TaxiConfig>()
    for (index in 0 until configs.length()) {
        taxiConfigs.add(ParseTaxiConfig(configs.getJSONObject(index), request))
    }

    return taxiConfigs
}

fun MakePlaceholders(jsonObject: JSONObject, request: UserRequest): Map<String, String> {
    val tariffs = jsonObject.getJSONArray("tariffs")


    var replacements = mutableMapOf<String, String>()
    replacements["departure_x"] = request.departure.point.latitude.toString()
    replacements["departure_y"] = request.departure.point.longitude.toString()
    replacements["arrival_x"] = request.arrival.point.latitude.toString()
    replacements["arrival_y"] = request.arrival.point.longitude.toString()
    replacements["tariff"] = tariffs.getString(request.tariff)
    replacements["timestamp"] = System.currentTimeMillis().toString()

    return replacements
}

fun ParseTaxiConfig(jsonObject: JSONObject, request: UserRequest): TaxiConfig {
    val replacements = MakePlaceholders(jsonObject, request)
    val config = ReplacePlaceholders(jsonObject, replacements)
    Log.v("Parsed Config", config.toString())

    val name = config.getString("name")
    val url = config.getString("url")
    val method = config.getString("method")
    val body = config.optString("body", "")

    val headersArr = config.optJSONArray("headers")
    val headers = mutableListOf<RequestHeaders>()
    if (headersArr != null) {
        for (i in 0 until headersArr.length()) {
            val header = headersArr.getJSONObject(i)
            headers.add(RequestHeaders(header.getString("name"), header.getString("value")))
        }
    }

    val pricePath = config.optString("price", "")
    val waitTimePath = config.optString("waiting_time", "")

    return TaxiConfig(name, url, method, body, headers, pricePath, waitTimePath)
}

//fun ParseTaxiConfig2(jsonStr: String, replacements: Map<String, String>): TaxiConfig {
//    val json = JSONObject(jsonStr)
//
//    // Extract fields
//    val name = json.getString("name")
//    val url = json.getString("url")
//
//    // Convert body (JSONObject) to string and replace placeholders
//    val bodyJson = json.getJSONObject("body").toString()
//    val body = ReplacePlaceholders(bodyJson, replacements)
//
//    // Process headers
//    val headersArr = json.getJSONArray("headers")
//    val headers = mutableListOf<RequestHeaders>()
//    for (i in 0 until headersArr.length()) {
//        val header = headersArr.getJSONObject(i)
//        headers.add(RequestHeaders(header.getString("name"), header.getString("value")))
//    }
//
//    return TaxiConfig(name, url, body, headers)
//}

fun ReplacePlaceholders(jsonObject: JSONObject, replacements: Map<String, String>): JSONObject {
    val input = jsonObject.toString()

    return JSONObject(input.replace(Regex(""""?\{\{(.*?)\}\}"?""")) { matchResult ->
        val key = matchResult.groupValues[1]
        replacements[key] ?: matchResult.value
    })
}

fun ReplacePlaceholders2(jsonObject: JSONObject, replacements: Map<String, String>): JSONObject {
    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.get(key)
        Log.v("Bober key", value.toString())

        if (value is String && value.startsWith("{{") && value.endsWith("}}")) {
            val placeholder = value.substring(2, value.length - 2)
            val actualValue = replacements[placeholder]
            if (actualValue != null) {
                jsonObject.put(key, actualValue)
            }
        }
    }
    return jsonObject
}

fun loadJSONFromAsset(): JSONObject {
    val testJson = """
        {
          "configs": [
            {
              "name": "Таксовичкофф",
              "url": "https://api.gruzovichkof.ru/3/calculator/prices?v=2",
              "method": "post",
              "body": {
                "loc": [
                  {
                    "lat":"{{departure_x}}",
                    "lng":"{{departure_y}}"
                  },
                  {
                    "lat":"{{arrival_x}}",
                    "lng":"{{arrival_y}}"
                  }
                ],
                "paymentType": 0,
                "date":"{{timestamp}}",
                "ordertime":618,
                "carOptions": {
                  "car_id":1,
                  "passengers":0,
                  "porters":null
                },
                "options":[]
              },
              "headers": [
                {
                  "name": "accept",
                  "value": "application/json, text/plain, */*"
                },
                {
                  "name": "accept-language",
                  "value": "ru,en;q=0.9"
                },
                {
                  "name": "api-key",
                  "value": "${BuildConfig.TAKSOVICHKOFF_API_KEY}"
                },
                {
                  "name": "content-type",
                  "value": "application/json"
                },
                {
                  "name": "origin",
                  "value": "https://msk.taxovichkof.ru"
                },
                {
                  "name": "priority",
                  "value": "u=1, i"
                },
                {
                  "name": "referer",
                  "value": "https://msk.taxovichkof.ru/"
                },
                {
                  "name": "sec-ch-ua",
                  "value": "\"Chromium\";v=\"128\", \"Not;A=Brand\";v=\"24\", \"YaBrowser\";v=\"24.10\", \"Yowser\";v=\"2.5\""
                },
                {
                  "name": "sec-ch-ua-mobile",
                  "value": "?0"
                },
                {
                  "name": "sec-ch-ua-platform",
                  "value": "macOS"
                },
                {
                  "name": "sec-fetch-dest",
                  "value": "empty"
                },
                {
                  "name": "sec-fetch-mode",
                  "value": "cors"
                },
                {
                  "name": "sec-fetch-site",
                  "value": "cross-site"
                },
                {
                  "name": "user-agent",
                  "value": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 YaBrowser/24.10.0.0 Safari/537.36"
                }
              ],
              "tariffs": [
                1,
                2,
                3,
                4
              ],
              "price": "data/prices/{{tariff}}/!price"
            },
            {
              "name": "Яндекс",
              "url": "https://taxi-routeinfo.taxi.yandex.net/taxi_info?clid=ak250418&rll={{departure_y}},{{departure_x}}~{{arrival_y}},{{arrival_x}}&class={{tariff}}\n",
              "headers": [
                {
                  "name": "accept",
                  "value": "application/json"
                },
                {
                  "name": "YaTaxi-Api-Key",
                  "value": "${BuildConfig.YA_TAXI_API_KEY}"
                }
              ],
              "method": "get",
              "tariffs": [
                "econom",
                "business",
                "comfortplus",
                "vip"
              ],
              "price": "[options]/0/!price",
              "waiting_time": "[options]/0/!waiting_time"
            }
          ]
        }
    """.trimIndent()
    return JSONObject(testJson)
}

