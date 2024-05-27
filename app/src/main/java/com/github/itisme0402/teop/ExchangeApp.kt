package com.github.itisme0402.teop

import android.app.Application
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.github.itisme0402.teop.core.balance.BalanceRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.Currency
import javax.inject.Inject

@HiltAndroidApp
class ExchangeApp : Application() {
    @Inject
    lateinit var balanceRepository: BalanceRepository

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        super.onCreate()
        performOneTimeInit()
    }

    private fun performOneTimeInit() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefs.getBoolean(KEY_IS_INITIALIZED, false)) {
            runBlocking(Dispatchers.IO) {
                balanceRepository.updateBalances(mapOf(Currency.getInstance("EUR") to 1000.toBigDecimal()))
            }
            prefs.edit {
                putBoolean(KEY_IS_INITIALIZED, true)
            }
        }
    }

    private companion object {
        const val KEY_IS_INITIALIZED = "is_initialized"
    }
}