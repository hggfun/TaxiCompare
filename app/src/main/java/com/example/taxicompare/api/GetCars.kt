package com.example.taxicompare.api

import com.yandex.mapkit.geometry.Point

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.math.*

fun GetCars(location: Point): List<Point> {
    val client = HttpClient(CIO) {
    }
    try {
        if (location == null) {
            val response = runBlocking {
                val HttpResponse = client.get("http://130.193.59.12:8080/get-cars") {
                    contentType(ContentType.Application.Json)
                    setBody("{\"location\": [${location.latitude}, ${location.longitude}]}")
                }
            }
        } else {
            throw Exception("Location already defined")
        }
    } catch (e: Exception) {
        return MakeStaticCars(location)
    } finally {
        client.close()
    }

    return emptyList()
}

fun MakeStaticCars(location: Point): List<Point> {
    val earthRadius = 6371000.0
    val prime1 = 31
    val prime2 = 53

    val baseSeed = location.latitude.toString().hashCode() * prime1 + location.longitude.toString().hashCode() * prime2

    fun detRand(seed: Int, mod: Int, offset: Int = 0): Int {
        return abs((seed + offset * 1299827) % mod)
    }

    val d1 = 10 + detRand(baseSeed, 11, 1)
    val d2 = 10 + detRand(baseSeed, 11, 2)
    val a1 = detRand(baseSeed, 360, 3)
    val a2 = detRand(baseSeed, 360, 4)

    fun move(latitude: Double, longitude: Double, distance: Double, bearing: Double): Point {
        val brngRad = Math.toRadians(bearing)
        val latitudeRad = Math.toRadians(latitude)
        val longitudeRad = Math.toRadians(longitude)

        val newlatitude = asin(sin(latitudeRad) * cos(distance / earthRadius)
                + cos(latitudeRad) * sin(distance / earthRadius) * cos(brngRad))
        val newlongitude = longitudeRad + atan2(
            sin(brngRad) * sin(distance / earthRadius) * cos(latitudeRad),
            cos(distance / earthRadius) - sin(latitudeRad) * sin(newlatitude)
        )
        return Point(Math.toDegrees(newlatitude), Math.toDegrees(newlongitude))
    }

    return listOf(
        move(location.latitude, location.longitude, d1.toDouble(), a1.toDouble()),
        move(location.latitude, location.longitude, d2.toDouble(), a2.toDouble())
    )
}
