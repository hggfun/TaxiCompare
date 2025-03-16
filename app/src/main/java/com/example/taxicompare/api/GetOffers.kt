package com.example.taxicompare.api

import com.example.taxicompare.R
import com.example.taxicompare.model.TripOffer

fun GetOffers(): List<TripOffer> {
    val sampleTripOffers = listOf(
        TripOffer(iconResId = R.drawable.yandex, companyName = "Yandex", price = "209", tripTime = "15"),
        TripOffer(iconResId = R.drawable.citimobil, companyName = "Ситимобил", price = "180", tripTime = "12"),
        TripOffer(iconResId = R.drawable.taksovichkof, companyName = "Таксовичкоф", price = "224", tripTime = "20"),
        TripOffer(iconResId = R.drawable.maxim, companyName = "Maxim", price = "200", tripTime = "15"),
        TripOffer(iconResId = R.drawable.omega, companyName = "Omega", price = "189", tripTime = "12")
    )
    return sampleTripOffers
}