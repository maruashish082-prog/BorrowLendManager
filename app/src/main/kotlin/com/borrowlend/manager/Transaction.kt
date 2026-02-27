package com.borrowlend.manager

data class Transaction(
    val id: Long = 0,
    val name: String,
    val type: String,   // "BORROW" or "LEND"
    val amount: Double,
    val date: String,
    val remarks: String
)
