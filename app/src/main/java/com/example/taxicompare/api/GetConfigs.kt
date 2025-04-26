package com.example.taxicompare.api

import android.util.Log
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
import org.json.JSONObject

suspend fun GetConfigs(request: UserRequest): List<TaxiConfig> {
    val client = HttpClient(CIO)

    val response: HttpResponse = client.get("http://130.193.59.88:8080/get-config") {
        contentType(ContentType.Application.Json)
        setBody("{\"type\": \"taxi\", \"name\": \"yandex\"}")
    }
    Log.v("TestingGetConfigs", response.bodyAsText())

    return emptyList()
}

//fun MakePlaceholders(request: UserRequest): Map<String, String> {
//
//}


fun ParseTaxiConfig(jsonStr: String, replacements: Map<String, String>): TaxiConfig {
    val json = JSONObject(jsonStr)

    // Extract fields
    val name = json.getString("name")
    val url = json.getString("url")

    // Convert body (JSONObject) to string and replace placeholders
    val bodyJson = json.getJSONObject("body").toString()
    val body = ReplacePlaceholders(bodyJson, replacements)

    // Process headers
    val headersArr = json.getJSONArray("headers")
    val headers = mutableListOf<RequestHeaders>()
    for (i in 0 until headersArr.length()) {
        val header = headersArr.getJSONObject(i)
        headers.add(RequestHeaders(header.getString("name"), header.getString("value")))
    }

    return TaxiConfig(name, url, body, headers)
}

fun ReplacePlaceholders(input: String, replacements: Map<String, String>): String {
    return input.replace(Regex("""\{\{(.*?)\}\}""")) { matchResult ->
        val key = matchResult.groupValues[1]
        replacements[key] ?: matchResult.value
    }
}
