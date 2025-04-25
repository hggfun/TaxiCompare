package com.example.taxicompare.api

import android.util.Log
import android.widget.Toast
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

suspend fun GetPricePredict(): List<Int> {
    val client = HttpClient(CIO)
//    val response: HttpResponse = client.get("https://ktor.io/")
//    return response.bodyAsText()
//        .split(',')
//        .map { it.toIntOrNull() ?: 0 }

//    val response2: HttpResponse = client.get("http://130.193.59.88:8080/get_config") {
//        url {
//            parameters.append("taxi", "yandex")
//        }
//    }
//    Log.v("Testing", "ready to get response2.bodyAsText()")
//    Log.v("Testing", response2.bodyAsText())

    val response4: HttpResponse = client.get("http://130.193.59.88:8080/ping")
    Log.v("Testing", response4.request.content.toString())
    Log.v("Testing", response4.status.value.toString())
    Log.v("Testing", response4.bodyAsText())



    val response3: HttpResponse = client.get("http://130.193.59.88:8080/get-config") {
        contentType(ContentType.Application.Json)
        setBody("{\"type\": \"taxi\", \"name\": \"yandex\"}")
    }
    Log.v("Testing", "ready to get response3.bodyAsText()")
    Log.v("Testing", response3.request.content.toString())
    Log.v("Testing", response3.status.value.toString())
    Log.v("Testing", response3.bodyAsText())

    val jokeResponse: HttpResponse = client.get("https://api.chucknorris.io/jokes/random")
    Log.v("Testing", "ready to get joke .bodyAsText()")
    Log.v("Testing", jokeResponse.bodyAsText())

    client.close()
    return listOf(250, 260, 280, 200)
}