package com.github.itisme0402.teop.network

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class RemoteExchangeRateInfo(
    @SerializedName("base")
    val base: String,
    @SerializedName("rates")
    val rates: Map<String, BigDecimal>
)
