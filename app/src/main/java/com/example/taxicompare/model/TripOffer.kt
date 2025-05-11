package com.example.taxicompare.model

import com.yandex.mapkit.geometry.Point
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

data class TripOffer(
    val iconResId: Int,
    val companyName: String,
    var price: String,
    var tripTime: String?
)

@Serializable
data class ExtendedTripInfo(
    val departure: Point,
    val arrival: Point,
    val timestamp: Long,
    val distance: Int,
    val duration: Int
)
