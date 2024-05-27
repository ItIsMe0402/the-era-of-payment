package com.github.itisme0402.teop.core.exchange.calculator

import com.github.itisme0402.teop.core.MoneyAmount
import com.github.itisme0402.teop.core.history.TransactionCountDataSource
import java.math.RoundingMode
import javax.inject.Inject

class FeeRepositoryImpl @Inject constructor(
    private val transactionCountDataSource: TransactionCountDataSource,
) : FeeRepository {
    override fun calculateFee(
        amountToSell: MoneyAmount,
        amountToReceive: MoneyAmount
    ): MoneyAmount? {
        val currentTransactionCount = transactionCountDataSource.getCurrentTransactionCount()
        return if (currentTransactionCount > MAX_TRANSACTIONS_WITHOUT_FEE) {
            MoneyAmount(
                (amountToSell.amount * STANDARD_FEE).setScale(amountToSell.currency.defaultFractionDigits, RoundingMode.UP),
                amountToSell.currency,
            )
        } else {
            null
        }
    }

    private companion object {
        const val MAX_TRANSACTIONS_WITHOUT_FEE = 5
        val STANDARD_FEE = "0.007".toBigDecimal()
    }
}