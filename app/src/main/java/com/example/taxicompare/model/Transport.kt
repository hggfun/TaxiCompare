package com.example.taxicompare.model

import com.yandex.mapkit.geometry.Point

data class Transport(
    val point: Point,
    val companyName: String,
    var price: String
)