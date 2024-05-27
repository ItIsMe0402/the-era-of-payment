package com.github.itisme0402.teop

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.github.itisme0402.teop.core.history.TransactionCountDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesTransactionCountDataSource @Inject constructor(
    @ApplicationContext
    appContext: Context,
) : TransactionCountDataSource {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun getCurrentTransactionCount(): Int {
        return sharedPreferences.getInt(KEY_TRANSACTION_COUNT, 0)
    }

    override fun incrementTransactionCount() {
        val currentTransactionCount = getCurrentTransactionCount()
        sharedPreferences.edit {
            putInt(KEY_TRANSACTION_COUNT, currentTransactionCount + 1)
        }
    }

    private companion object {
        const val KEY_TRANSACTION_COUNT = "transaction_count"
    }
}