package com.example.taxicompare.model

data class TaxiConfig(
    val name: String,
    var url: String,
    var method: String,
    var body: String,
    var headers: List<RequestHeaders>,
    var pricePath: String,
    var waitTimePath: String
)

data class RequestHeaders(
    val name: String,
    val value: String
)
