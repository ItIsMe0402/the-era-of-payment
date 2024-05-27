package com.github.itisme0402.teop.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Currency

@Entity(tableName = LocalCurrencyBalance.TABLE_NAME)
data class LocalCurrencyBalance(
    @PrimaryKey
    @ColumnInfo(name = COL_CURRENCY)
    val currency: Currency,
    @ColumnInfo(name = COL_BALANCE)
    val balance: BigDecimal,
) {

    companion object {
        const val TABLE_NAME = "currency_balance"
        const val COL_CURRENCY = "currency"
        const val COL_BALANCE = "balance"
    }
}
