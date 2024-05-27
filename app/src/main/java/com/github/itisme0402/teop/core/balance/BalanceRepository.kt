package com.github.itisme0402.teop.core.balance

import com.github.itisme0402.teop.core.MoneyAmount
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Currency

interface BalanceRepository {
    fun getBalances(): Flow<Map<Currency, MoneyAmount>>
    suspend fun updateBalances(newBalances: Map<Currency, BigDecimal>)
}
