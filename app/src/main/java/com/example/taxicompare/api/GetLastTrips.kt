package com.example.taxicompare.api

fun GetLastTrips(): List<Pair<String, String>> {
    return listOf(
        Pair("Большая филевская, 16", "Покровский бульвар, 11с1"),
        Pair("Арена Химки", "Яндекс Маркет"),
        Pair("Донелайтиса, 34", "Киевская, 19"),
        Pair("Новоостаповская, 4к1", "Фармленд"),
    )
}

fun GetLastAddresses(): List<String> {
    return listOf(
        "Большая филевская, 16",
        "Покровский бульвар, 11с1",
        "Арена Химки",
        "Яндекс Маркет",
        "Донелайтиса, 34"
    )
}