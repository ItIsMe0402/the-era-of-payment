package com.github.itisme0402.teop.core.exchange

import com.github.itisme0402.teop.core.MoneyAmount
import com.github.itisme0402.teop.core.history.TransactionRepository
import java.util.Currency
import javax.inject.Inject

class ExchangeUseCase @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun performExchange(amountToSell: MoneyAmount, currencyToReceive: Currency): ExchangeData {
        return exchangeRepository.performExchange(amountToSell, currencyToReceive).also {
            transactionRepository.incrementTransactionCount()
        }
    }
}