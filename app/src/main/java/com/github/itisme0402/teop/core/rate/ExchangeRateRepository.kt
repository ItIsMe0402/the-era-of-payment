package com.github.itisme0402.teop.core.rate

import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface ExchangeRateRepository {
    fun observeExchangeRates(): Flow<ExchangeRateInfo>
    suspend fun syncExchangeRates()
    fun observeAvailableCurrencies(): Flow<Set<Currency>>
}
