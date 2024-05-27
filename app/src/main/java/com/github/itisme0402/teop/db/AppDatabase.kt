package com.github.itisme0402.teop.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LocalCurrencyBalance::class], version = 1)
@TypeConverters(BigDecimalTypeConverter::class, CurrencyTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun balanceDao(): BalanceDao

    companion object {
        fun create(appContext: Context): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, "exchange").build()
        }
    }
}