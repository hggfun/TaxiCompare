package com.example.taxicompare.model

import kotlinx.serialization.Serializable


data class TripOffer(
    val iconResId: Int,
    val companyName: String,
    var price: String,
    var tripTime: String?
)

@Serializable
data class ExtendedTripInfo(
    val start_point_x: Double,
    val start_point_y: Double,
    val end_point_x: Double,
    val end_point_y: Double,
    val timestamp: Long,
    val distance: Int,
    val duration: Int
)
