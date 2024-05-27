package com.github.itisme0402.teop.core.rate

import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface LocalExchangeRateDataSource {
    fun observeExchangeRates(): Flow<ExchangeRateInfo>
    suspend fun saveExchangeRates(rates: ExchangeRateInfo)
    fun observeAvailableCurrencies(): Flow<Set<Currency>>
}
