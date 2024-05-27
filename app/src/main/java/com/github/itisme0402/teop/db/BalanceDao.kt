package com.github.itisme0402.teop.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {

    @Transaction
    @Query("SELECT * FROM ${LocalCurrencyBalance.TABLE_NAME}")
    fun observeBalances(): Flow<List<LocalCurrencyBalance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBalances(balances: List<LocalCurrencyBalance>)
}
