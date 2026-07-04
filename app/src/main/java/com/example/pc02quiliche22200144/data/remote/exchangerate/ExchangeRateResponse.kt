package com.example.pc02quiliche22200144.data.remote.exchangerate

data class ExchangeRateResponse(
    val result: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>
)
