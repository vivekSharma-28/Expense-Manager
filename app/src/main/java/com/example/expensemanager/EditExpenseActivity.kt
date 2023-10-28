package com.example.expensemanager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.Model.ExpenseModel
import com.example.expensemanager.databinding.ActivityEditExpenseBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Suppress("DEPRECATION")
class EditExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditExpenseBinding
    private lateinit var type: String
    private lateinit var expenseModel: ExpenseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        expenseModel = intent.getSerializableExtra("model") as ExpenseModel
        type = expenseModel.type!!
        binding.amount.setText(expenseModel.amount.toString())
        binding.category.setText(expenseModel.category.toString())
        binding.Note.setText(expenseModel.note.toString())
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
        menuInflater.inflate(R.menu.update, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update -> updateDialog()
            R.id.delete -> deleteDialog()
        }
        return false
    }

    private fun updateDialog(){
        val bottomSheetDialog=BottomSheetDialog(this@EditExpenseActivity)
        bottomSheetDialog.setContentView(R.layout.update_dialog)

        bottomSheetDialog.findViewById<TextView>(R.id.textview_yes)?.setOnClickListener {
            updateExpense()
            bottomSheetDialog.dismiss()
            finish()
        }

        bottomSheetDialog.findViewById<TextView>(R.id.textview_no)?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun deleteDialog(){
        val bottomSheet = BottomSheetDialog(this@EditExpenseActivity)
        bottomSheet.setContentView(R.layout.delete_dialog)

        bottomSheet.findViewById<TextView>(R.id.textview_yes)?.setOnClickListener {
            deleteExpense()
            bottomSheet.dismiss()
            finish()
        }

        bottomSheet.findViewById<TextView>(R.id.textview_no)?.setOnClickListener {
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun deleteExpense() {
        expenseModel.expenseId?.let {
            FirebaseFirestore.getInstance().collection("expenses").document(it).delete()
        }
    }

    private fun updateExpense() {
        val expenseId = expenseModel.expenseId
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
        val model = FirebaseAuth.getInstance().uid?.let {
            ExpenseModel(
                expenseId = expenseId,
                note = note,
                category = category,
                amount = amount.toLong(),
                type = type,
                uid = it,
                time = expenseModel.time
            )
        }

        if (model != null) {
            if (expenseId != null) {
                FirebaseFirestore.getInstance().collection("expenses").document(expenseId)
                    .set(model)
            }
        }
    }
}