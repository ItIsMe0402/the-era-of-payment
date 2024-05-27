package com.github.itisme0402.teop.core.exchange.calculator

import com.github.itisme0402.teop.core.MoneyAmount

interface FeeRepository {
    fun calculateFee(amountToSell: MoneyAmount, amountToReceive: MoneyAmount): MoneyAmount?
}
