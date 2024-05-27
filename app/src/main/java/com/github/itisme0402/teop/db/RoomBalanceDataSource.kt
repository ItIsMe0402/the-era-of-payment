package com.github.itisme0402.teop.db

import com.github.itisme0402.teop.core.balance.LocalBalanceDataSource
import com.github.itisme0402.teop.core.MoneyAmount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

class RoomBalanceDataSource @Inject constructor(
    private val balanceDao: BalanceDao,
) : LocalBalanceDataSource {
    override fun getBalances(): Flow<Map<Currency, MoneyAmount>> =
        balanceDao.observeBalances().map {
            it.associate { (currency, balance) -> currency to MoneyAmount(balance, currency) }
        }

    override suspend fun updateBalances(balances: Map<Currency, BigDecimal>) {
        balanceDao.updateBalances(balances.map { (currency, balance) -> LocalCurrencyBalance(currency, balance) })
    }
}