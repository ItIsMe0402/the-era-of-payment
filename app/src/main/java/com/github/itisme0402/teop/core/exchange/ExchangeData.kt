package com.github.itisme0402.teop.core.exchange

import com.github.itisme0402.teop.core.MoneyAmount

data class ExchangeData(
    val amountReceived: MoneyAmount,
    val fee: MoneyAmount?,
)
