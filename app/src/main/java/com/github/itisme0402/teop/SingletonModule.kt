package com.github.itisme0402.teop

import android.content.Context
import com.github.itisme0402.teop.core.balance.BalanceRepository
import com.github.itisme0402.teop.core.balance.BalanceRepositoryImpl
import com.github.itisme0402.teop.core.rate.ExchangeRateRepository
import com.github.itisme0402.teop.core.rate.ExchangeRateRepositoryImpl
import com.github.itisme0402.teop.core.exchange.ExchangeRepository
import com.github.itisme0402.teop.core.exchange.ExchangeRepositoryImpl
import com.github.itisme0402.teop.core.exchange.calculator.FeeRepository
import com.github.itisme0402.teop.core.exchange.calculator.FeeRepositoryImpl
import com.github.itisme0402.teop.core.balance.LocalBalanceDataSource
import com.github.itisme0402.teop.core.rate.LocalExchangeRateDataSource
import com.github.itisme0402.teop.core.rate.RemoteExchangeRateDataSource
import com.github.itisme0402.teop.core.history.TransactionCountDataSource
import com.github.itisme0402.teop.core.history.TransactionRepository
import com.github.itisme0402.teop.core.history.TransactionRepositoryImpl
import com.github.itisme0402.teop.db.AppDatabase
import com.github.itisme0402.teop.db.BalanceDao
import com.github.itisme0402.teop.db.RoomBalanceDataSource
import com.github.itisme0402.teop.network.NetworkApiExchangeRateDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [SingletonModule.Binders::class])
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.create(appContext)
    }

    @Provides
    fun provideBalanceDao(db: AppDatabase): BalanceDao = db.balanceDao()

    @Module
    @InstallIn(SingletonComponent::class)
    interface Binders {

        @Binds
        fun bindBalanceRepository(impl: BalanceRepositoryImpl): BalanceRepository

        @Binds
        fun bindExchangeRepository(impl: ExchangeRepositoryImpl): ExchangeRepository

        @Binds
        fun bindLocalBalanceDataSource(impl: RoomBalanceDataSource): LocalBalanceDataSource

        @Binds
        fun bindExchangeRateRepository(impl: ExchangeRateRepositoryImpl): ExchangeRateRepository

        @Binds
        @Singleton
        fun bindLocalExchangeRateDataSource(impl: InMemoryExchangeRateDataSource): LocalExchangeRateDataSource

        @Binds
        fun bindRemoteExchangeRateDataSource(impl: NetworkApiExchangeRateDataSource): RemoteExchangeRateDataSource

        @Binds
        fun bindFeeRepository(impl: FeeRepositoryImpl): FeeRepository

        @Binds
        @Singleton
        fun bindTransactionCountDataSource(impl: SharedPreferencesTransactionCountDataSource): TransactionCountDataSource

        @Binds
        fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
    }
}