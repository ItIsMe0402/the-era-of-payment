package com.github.itisme0402.teop.core.balance

import com.github.itisme0402.teop.core.MoneyAmount
import kotlinx.coroutines.flow.Flow
import java.util.Currency
import javax.inject.Inject

class BalanceUseCase @Inject constructor(
    private val balanceRepository: BalanceRepository,
) {
    fun getBalances(): Flow<Map<Currency, MoneyAmount>> = balanceRepository.getBalances()
}
