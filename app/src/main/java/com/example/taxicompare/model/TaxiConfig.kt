package com.example.taxicompare.model

data class TaxiConfig(
    val name: String,
    var url: String,
    var body: String,
    var headers: List<RequestHeaders>
)

data class RequestHeaders(
    val name: String,
    val value: String
)
