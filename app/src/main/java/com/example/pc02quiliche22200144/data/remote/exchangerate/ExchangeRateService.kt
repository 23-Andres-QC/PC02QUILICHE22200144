package com.example.pc02quiliche22200144.data.remote.exchangerate

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {
    @GET("v6/{apiKey}/latest/{baseCode}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCode") baseCode: String
    ): ExchangeRateResponse
}
