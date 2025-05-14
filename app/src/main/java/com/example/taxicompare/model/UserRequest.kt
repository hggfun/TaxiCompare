package com.example.taxicompare.model

import kotlinx.serialization.Serializable


data class UserRequest(
    val location: Point?,
    val arrival: Address,
    val departure: Address,
    val tariff: Int
)

data class Address(
    val name: String,
    val point: Point
)

@Serializable
data class Point(
    var latitude: Double,
    var longitude: Double
)
