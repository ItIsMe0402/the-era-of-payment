package com.github.itisme0402.teop.core.exchange.calculator

import com.github.itisme0402.teop.core.exchange.ExchangeData
import com.github.itisme0402.teop.core.MoneyAmount
import com.github.itisme0402.teop.core.rate.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import javax.inject.Inject

class ExchangeCalculator @Inject constructor(
    private val rateRepository: ExchangeRateRepository,
    private val feeRepository: FeeRepository,
) {
    fun getCalculationFlow(
        amountToSell: MoneyAmount,
        currencyToReceive: Currency,
    ): Flow<Result<ExchangeData>> {
        if (amountToSell.currency == currencyToReceive) {
            throw IllegalArgumentException("Currency to sell matches target currency ($currencyToReceive)")
        }
        if (amountToSell.amount <= BigDecimal.ZERO) {
            throw IllegalArgumentException(
                "Amount to sell should be strictly positive; actual: ${amountToSell.amount}"
            )
        }
        return rateRepository.observeExchangeRates().map { exchangeRates ->
            try {
                val currencyToSell = amountToSell.currency
                val targetToBaseRate = exchangeRates.rates[currencyToReceive]
                    ?: throw NullPointerException(
                        "Failed to find rate for target currency ($currencyToReceive)"
                    )
                val baseAmount = if (currencyToSell == exchangeRates.base) {
                    amountToSell.amount
                } else {
                    val soldToBaseRate = exchangeRates.rates[currencyToSell]
                        ?: throw NullPointerException(
                            "Failed to find rate for sold currency ($currencyToSell)"
                        )
                    amountToSell.amount.divide(
                        soldToBaseRate,
                        exchangeRates.base.defaultFractionDigits,
                        RoundingMode.DOWN
                    )
                }
                val amountReceived = MoneyAmount(
                    (baseAmount * targetToBaseRate)
                        .setScale(currencyToReceive.defaultFractionDigits, RoundingMode.DOWN),
                    currencyToReceive
                )
                val fee = feeRepository.calculateFee(amountToSell, amountReceived)
                return@map Result.success(ExchangeData(amountReceived, fee))
            } catch (e: Throwable) {
                return@map Result.failure(e)
            }
        }
    }

    suspend fun calculateExchange(amountToSell: MoneyAmount, currencyToReceive: Currency): ExchangeData =
        getCalculationFlow(amountToSell, currencyToReceive).first().getOrThrow()

    fun observeAvailableCurrencies() = rateRepository.observeAvailableCurrencies()
}
