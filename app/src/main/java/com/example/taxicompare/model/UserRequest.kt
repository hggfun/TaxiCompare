package com.example.taxicompare.model

import com.yandex.mapkit.geometry.Point

data class UserRequest(
    val location: Point?,
    val arrival: Point?,
    val departure: Point?,
    val tariff: Int
)
