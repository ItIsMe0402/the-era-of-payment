package com.github.itisme0402.teop.core.rate

import java.math.BigDecimal
import java.util.Currency

data class ExchangeRateInfo(
    val base: Currency,
    val rates: Map<Currency, BigDecimal>
)
