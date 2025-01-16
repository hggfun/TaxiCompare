package com.example.taxicompare.api

import com.example.taxicompare.R
import com.example.taxicompare.model.TripOffer

fun GetOffers(): List<TripOffer> {
    val sampleTripOffers = listOf(
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 1", price = "20", tripTime = "15 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 2", price = "18", tripTime = "12 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 3", price = "22", tripTime = "20 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 1", price = "20", tripTime = "15 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 2", price = "18", tripTime = "12 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 3", price = "22", tripTime = "20 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 1", price = "20", tripTime = "15 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 2", price = "18", tripTime = "12 min"),
        TripOffer(iconResId = R.drawable.adv8, companyName = "Taxi Co 3", price = "22", tripTime = "20 min"),
        TripOffer(R.drawable.adv8, "Yandex", "100", "15")
    )
    return sampleTripOffers
}