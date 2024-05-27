package com.github.itisme0402.teop.network

import retrofit2.http.GET

interface NetworkApi {
    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getExchangeRates(): RemoteExchangeRateInfo
}
