package com.github.itisme0402.teop.db

import androidx.room.TypeConverter
import java.util.Currency

class CurrencyTypeConverter {

    @TypeConverter
    fun stringToCurrency(input: String): Currency = Currency.getInstance(input)

    @TypeConverter
    fun currencyToString(input: Currency): String = input.toString()
}
