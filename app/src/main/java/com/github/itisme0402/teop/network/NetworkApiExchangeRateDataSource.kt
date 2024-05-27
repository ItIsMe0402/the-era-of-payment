package com.github.itisme0402.teop.network

import com.github.itisme0402.teop.core.rate.ExchangeRateInfo
import com.github.itisme0402.teop.core.rate.RemoteExchangeRateDataSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Currency
import javax.inject.Inject

class NetworkApiExchangeRateDataSource @Inject constructor() : RemoteExchangeRateDataSource {
    private val api: NetworkApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NetworkApi::class.java)

    override suspend fun getExchangeRates(): ExchangeRateInfo {
        val remoteExchangeRateInfo = api.getExchangeRates()
        return ExchangeRateInfo(
            Currency.getInstance(remoteExchangeRateInfo.base),
            remoteExchangeRateInfo.rates.mapKeys { (currencyCode, _) -> Currency.getInstance(currencyCode) }
        )
    }

    private companion object {
        // Kinda obfuscating the URL just for fun
        val BASE_URL = "https://developers.${"o`xrdq`".map { it + 1 }.fold("") { s, c -> s + c}}.com/"
    }
}