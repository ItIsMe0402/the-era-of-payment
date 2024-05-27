package com.github.itisme0402.teop.core.history

interface TransactionCountDataSource {
    fun getCurrentTransactionCount(): Int
    fun incrementTransactionCount()
}
