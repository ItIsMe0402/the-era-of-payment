package com.github.itisme0402.teop

import com.github.itisme0402.teop.core.rate.ExchangeRateInfo
import com.github.itisme0402.teop.core.rate.LocalExchangeRateDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.util.Currency
import javax.inject.Inject

class InMemoryExchangeRateDataSource @Inject constructor() : LocalExchangeRateDataSource {
    private val exchangeRatesFlow = MutableStateFlow<ExchangeRateInfo?>(null)

    override fun observeAvailableCurrencies(): Flow<Set<Currency>> {
        return exchangeRatesFlow.map { it?.rates?.keys.orEmpty() }
    }

    override fun observeExchangeRates(): Flow<ExchangeRateInfo> {
        return exchangeRatesFlow.filterNotNull()
    }

    override suspend fun saveExchangeRates(rates: ExchangeRateInfo) {
        exchangeRatesFlow.value = rates
    }
}