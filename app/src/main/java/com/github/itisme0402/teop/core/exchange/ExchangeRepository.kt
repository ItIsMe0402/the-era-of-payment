package com.github.itisme0402.teop.core.exchange

import com.github.itisme0402.teop.core.MoneyAmount
import java.util.Currency

interface ExchangeRepository {
    suspend fun performExchange(amountToSell: MoneyAmount, currencyToReceive: Currency): ExchangeData
}
