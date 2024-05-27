package com.github.itisme0402.teop.core

import java.math.BigDecimal
import java.util.Currency

data class MoneyAmount(
    val amount: BigDecimal,
    val currency: Currency,
)
