package com.example.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.expensemanager.Model.ExpenseModel
import com.example.expensemanager.databinding.ActivityAddExpenseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.UUID

@Suppress("DEPRECATION")
class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var type: String
//    private lateinit var expenseModel: ExpenseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.show()

        type = intent.getStringExtra("type").toString()

        binding.apply {
            if (type == "Income") {
                incomeRadio.isChecked = true
            } else {
                expenseRadio.isChecked = true
            }
            incomeRadio.setOnClickListener {
                type = "Income"
            }
            expenseRadio.setOnClickListener {
                type = "Expense"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.saveExpense) {
            createExpense()
            return true
        }
        return false
    }

    private fun createExpense() {
        val expenseId = UUID.randomUUID().toString()
        val amount = binding.amount.text.toString().trim()
        val note = binding.Note.text.toString().trim()
        val category = binding.category.text.toString().trim()
        val incomeChecked = binding.incomeRadio.isChecked

        type = if (incomeChecked) {
            "Income"
        } else "Expense"

        if (amount.isEmpty()) {
            binding.amount.error = "Fill The Amount"
            return
        }
        val expenseModel = FirebaseAuth.getInstance().uid?.let {
            ExpenseModel(
                expenseId = expenseId,
                note = note,
                category = category,
                amount = amount.toLong(),
                type = type,
                uid = it,
                time = Calendar.getInstance().timeInMillis
            )
        }

        if (expenseModel != null) {
            FirebaseFirestore.getInstance().collection("expenses").document(expenseId)
                .set(expenseModel)
        }

        finish()
    }

}