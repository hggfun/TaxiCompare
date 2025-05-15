package com.example.taxicompare.testingdata

import com.example.taxicompare.R
import com.example.taxicompare.model.TripOffer

fun MakeStaticOffers(entryPrice: Int): List<TripOffer> {
    fun randomPrice(percent: Int): Int {
        val min = (entryPrice * (1 - percent / 100.0)).toInt()
        val max = (entryPrice * (1 + percent / 100.0)).toInt()
        return (min..max).random()
    }
    return listOf(
        TripOffer(iconResId = R.drawable.citimobil, companyName = "Ситимобил", price = randomPrice(20).toString(), tripTime = null),
        TripOffer(iconResId = R.drawable.maxim, companyName = "Maxim", price = randomPrice(20).toString(), tripTime = null),
        TripOffer(iconResId = R.drawable.omega, companyName = "Omega", price = randomPrice(20).toString(), tripTime = null)
    )
}

fun MakeStaticCarsharingPrice(): Int {
    return 19
}

fun MakeStaticKickharingPrice(): Int {
    return 9
}

fun GetTariffText(index: Int): String {
    val tariffs = listOf("Эконом", "Комфорт", "Кoмфорт плюс", "Бизнес")
    return tariffs[index]
}

fun GetTariffs(): List<String> {
    return listOf("Эконом", "Комфорт", "Кoмфорт плюс", "Бизнес")
}
