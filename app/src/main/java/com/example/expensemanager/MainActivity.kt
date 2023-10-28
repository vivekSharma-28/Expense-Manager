@file:Suppress("DEPRECATION")

package com.example.expensemanager

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.Adaptor.ExpensesAdaptor
import com.example.expensemanager.Interface.OnItemsClick
import com.example.expensemanager.Model.ExpenseModel
import com.example.expensemanager.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), OnItemsClick {
    private val fromBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_fab)
    }
    private val toBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_fab)
    }
    private val clockWiseAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.clockwise)
    }
    private val antiClockWiseAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.anticlockwise)
    }
    private val fromBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)
    }
    private val toBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var expensesAdaptor: ExpensesAdaptor
    private var expenseModel: ExpenseModel? = null
    private var income: Long = 0
    private var expense: Long = 0
    private var isExpanded = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expensesAdaptor = ExpensesAdaptor(this, this)

        binding.recycleView.adapter = expensesAdaptor

        binding.add.setOnClickListener {

            if (isExpanded) {
                shrinkFab()
            } else {
                expandFab()
            }

        }

        binding.apply {
            addIncome.setOnClickListener {
                val intent = Intent(this@MainActivity, AddExpenseActivity::class.java)
                intent.putExtra("type", "Income")
                startActivity(intent)
            }

            addExpense.setOnClickListener {
                val intent = Intent(this@MainActivity, AddExpenseActivity::class.java)
                intent.putExtra("type", "Expense")
                startActivity(intent)
            }

            incomeList.setOnClickListener {
                Toast.makeText(this@MainActivity,"In Progress",Toast.LENGTH_SHORT).show()
            }
            expenseList.setOnClickListener {
                Toast.makeText(this@MainActivity,"In Progress",Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun expandFab() {

        binding.apply {
            view.visibility = View.VISIBLE
            addIncome.visibility = View.VISIBLE
            addExpense.visibility = View.VISIBLE
            incomeList.visibility = View.VISIBLE
            expenseList.visibility = View.VISIBLE
            addExpenseText.visibility = View.VISIBLE
            addIncomeText.visibility = View.VISIBLE
            incomeListText.visibility = View.VISIBLE
            expenseListText.visibility = View.VISIBLE

            add.startAnimation(clockWiseAnim)
            addIncome.startAnimation(fromBottomFabAnim)
            addExpense.startAnimation(fromBottomFabAnim)
            incomeList.startAnimation(fromBottomFabAnim)
            expenseList.startAnimation(fromBottomFabAnim)
            addExpenseText.startAnimation(fromBottomFabAnim)
            addIncomeText.startAnimation(fromBottomFabAnim)
            incomeListText.startAnimation(fromBottomFabAnim)
            expenseListText.startAnimation(fromBottomFabAnim)
            view.startAnimation(fromBottomBgAnim)
        }

        isExpanded = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shrinkFab() {

        binding.apply {
            view.visibility = View.GONE

            addIncome.visibility = View.GONE
            addExpense.visibility = View.GONE
            incomeList.visibility = View.GONE
            expenseList.visibility = View.GONE
            addExpenseText.visibility = View.GONE
            addIncomeText.visibility = View.GONE
            incomeListText.visibility = View.GONE
            expenseListText.visibility = View.GONE

            add.startAnimation(antiClockWiseAnim)
            addIncome.startAnimation(toBottomFabAnim)
            addExpense.startAnimation(toBottomFabAnim)
            incomeList.startAnimation(toBottomFabAnim)
            expenseList.startAnimation(toBottomFabAnim)
            addExpenseText.startAnimation(toBottomFabAnim)
            addIncomeText.startAnimation(toBottomFabAnim)
            incomeListText.startAnimation(toBottomFabAnim)
            expenseListText.startAnimation(toBottomFabAnim)
            view.startAnimation(toBottomBgAnim)
        }

        isExpanded = false
    }

    override fun onStart() {
        super.onStart()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please")
        progressDialog.setMessage("Wait")
        progressDialog.setCancelable(false)
        if (FirebaseAuth.getInstance().currentUser == null) {
            progressDialog.show()
            FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener {
                progressDialog.cancel()
            }.addOnFailureListener {
                progressDialog.cancel()
                Toast.makeText(this@MainActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        income = 0
        expense = 0
        getData()
    }

    private fun getData() {
        FirebaseFirestore.getInstance().collection("expenses")
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid).get().addOnSuccessListener {
                expensesAdaptor.clear()
                val dataList = it.documents
                for (ds in dataList) {
                    expenseModel = ds.toObject(ExpenseModel::class.java)
                    if (expenseModel != null) {
                        if (expenseModel?.type.equals("Income")) {
                            income += expenseModel?.amount!!
                        } else {
                            expense += expenseModel?.amount!!
                        }
                    }
                    expensesAdaptor.add(expenseModel)
                }
                setGraph()
            }
    }

    private fun setGraph() {
        val pieEntryList = ArrayList<PieEntry>()
        val colorList = ArrayList<Int>()
        if (income.toInt() != 0) {
            pieEntryList.add(PieEntry(income.toFloat(), "Income"))
            colorList.add(resources.getColor(R.color.teal_700))
        }
        if (expense.toInt() != 0) {
            pieEntryList.add(PieEntry(expense.toFloat(), "Expense"))
            colorList.add(resources.getColor(R.color.red))
        }

        val pieDataSet = PieDataSet(pieEntryList, "${income - expense}")
        pieDataSet.colors = colorList
        pieDataSet.valueTextColor = resources.getColor(R.color.white)
        val pieData = PieData(pieDataSet)
        binding.graph.data = pieData
        binding.graph.invalidate()
    }

    override fun onClick(expenseModel: ExpenseModel?) {
        val intent = Intent(this@MainActivity, EditExpenseActivity::class.java)
        intent.putExtra("model", expenseModel)
        startActivity(intent)
    }
}