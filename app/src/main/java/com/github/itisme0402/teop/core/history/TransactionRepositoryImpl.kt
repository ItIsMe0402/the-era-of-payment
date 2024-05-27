package com.github.itisme0402.teop.core.history

import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionCountDataSource: TransactionCountDataSource,
) : TransactionRepository {
    override fun incrementTransactionCount() {
        transactionCountDataSource.incrementTransactionCount()
    }
}