package com.github.itisme0402.teop.core

import com.github.itisme0402.teop.core.rate.ExchangeRateRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import javax.inject.Inject

class SyncUseCase @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository,
) {
    suspend fun syncExchangeRate() {
        while (true) {
            try {
                exchangeRateRepository.syncExchangeRates()
            } catch (e: CancellationException) {
                throw e
            } catch (ignored: Exception) {
                //ignore
            }
            delay(5_000)
        }
    }
}