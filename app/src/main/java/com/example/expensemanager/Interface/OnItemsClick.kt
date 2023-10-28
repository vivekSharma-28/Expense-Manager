package com.example.expensemanager.Interface

import com.example.expensemanager.Model.ExpenseModel

interface OnItemsClick {
    fun onClick(expenseModel: ExpenseModel?)
}