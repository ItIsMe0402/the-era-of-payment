package com.github.itisme0402.teop.core.rate

interface RemoteExchangeRateDataSource {
    suspend fun getExchangeRates(): ExchangeRateInfo
}
