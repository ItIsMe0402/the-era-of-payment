package com.github.itisme0402.teop.core.rate

import kotlinx.coroutines.flow.Flow
import java.util.Currency
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(
    private val localCurrencyRateDataSource: LocalExchangeRateDataSource,
    private val remoteCurrencyRateDataSource: RemoteExchangeRateDataSource,
) : ExchangeRateRepository {

    override fun observeExchangeRates(): Flow<ExchangeRateInfo> {
        return localCurrencyRateDataSource.observeExchangeRates()
    }

    override suspend fun syncExchangeRates() {
        val exchangeRates = remoteCurrencyRateDataSource.getExchangeRates()
        localCurrencyRateDataSource.saveExchangeRates(exchangeRates)
    }

    override fun observeAvailableCurrencies(): Flow<Set<Currency>> {
        return localCurrencyRateDataSource.observeAvailableCurrencies()
    }
}