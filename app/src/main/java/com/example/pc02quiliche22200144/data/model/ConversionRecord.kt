package com.example.pc02quiliche22200144.data.model

import com.google.firebase.Timestamp

data class ConversionRecord(
    val uid: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val amount: Double = 0.0,
    val fromCurrency: String = "",
    val toCurrency: String = "",
    val result: Double = 0.0
)
