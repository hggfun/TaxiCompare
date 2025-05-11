package com.example.taxicompare.model

import com.yandex.mapkit.geometry.Point

data class UserRequest(
    val location: Point,
    val arrival: Address,
    val departure: Address,
    val tariff: Int
)

data class Address(
    val name: String,
    val point: Point
)
