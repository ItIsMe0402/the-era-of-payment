package com.github.itisme0402.teop.core.exchange

import com.github.itisme0402.teop.core.MoneyAmount
import com.github.itisme0402.teop.core.balance.BalanceRepository
import com.github.itisme0402.teop.core.exchange.calculator.ExchangeCalculator
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeCalculator: ExchangeCalculator,
    private val balanceRepository: BalanceRepository,
) : ExchangeRepository {

    override suspend fun performExchange(
        amountToSell: MoneyAmount,
        currencyToReceive: Currency
    ): ExchangeData {
        val exchangeData = exchangeCalculator.calculateExchange(
            amountToSell,
            currencyToReceive
        )
        val (amountReceived, fee) = exchangeData
        val currentBalances = balanceRepository.getBalances().first()
        val newBalances = mutableMapOf<Currency, BigDecimal>()
        for (moneyAmount in listOfNotNull(amountToSell, amountReceived, fee)) {
            val oldMoneyAmount = currentBalances[moneyAmount.currency]?.amount ?: BigDecimal.ZERO
            newBalances[moneyAmount.currency] = oldMoneyAmount
        }
        newBalances[amountToSell.currency] = newBalances[amountToSell.currency]!! - amountToSell.amount
        if (fee != null) {
            newBalances[fee.currency] = newBalances[fee.currency]!! - fee.amount
        }
        if (newBalances.any { (_, balance) -> balance < BigDecimal.ZERO }) {
            throw IllegalArgumentException("Insufficient balance!")
        }
        newBalances[amountReceived.currency] = newBalances[amountReceived.currency]!! + amountReceived.amount
        balanceRepository.updateBalances(newBalances)
        return exchangeData
    }
}