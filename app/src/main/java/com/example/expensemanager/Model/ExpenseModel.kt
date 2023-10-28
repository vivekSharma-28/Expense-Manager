package com.example.expensemanager.Model

import java.io.Serializable

data class ExpenseModel(
    var expenseId: String?=null,
    var note: String?=null,
    var category: String?=null,
    var type: String?=null,
    var amount: Long?=0,
    var time: Long?=0,
    var uid: String?=null
) : Serializable