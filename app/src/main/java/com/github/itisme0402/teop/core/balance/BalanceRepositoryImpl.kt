package com.github.itisme0402.teop.core.balance

import com.github.itisme0402.teop.core.MoneyAmount
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

class BalanceRepositoryImpl @Inject constructor(
    private val localDataSource: LocalBalanceDataSource,
) : BalanceRepository {
    override fun getBalances(): Flow<Map<Currency, MoneyAmount>> = localDataSource.getBalances()
    override suspend fun updateBalances(newBalances: Map<Currency, BigDecimal>) {
        localDataSource.updateBalances(newBalances)
    }
}