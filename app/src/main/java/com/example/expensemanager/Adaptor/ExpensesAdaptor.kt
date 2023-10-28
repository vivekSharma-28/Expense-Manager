package com.example.expensemanager.Adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.Interface.OnItemsClick
import com.example.expensemanager.Model.ExpenseModel
import com.example.expensemanager.R
import com.example.expensemanager.databinding.ExpenseRowBinding
import com.google.rpc.context.AttributeContext.Resource
import java.util.Date

@SuppressLint("NotifyDataSetChanged")
class ExpensesAdaptor(private var context: Context, private var onItemsClick: OnItemsClick) :
    RecyclerView.Adapter<ExpensesAdaptor.UserViewHolder>() {

    private var expenseList = ArrayList<ExpenseModel?>()

    inner class UserViewHolder(val binding: ExpenseRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun add(expenseModel: ExpenseModel?) {
        expenseList.add(expenseModel)
        notifyDataSetChanged()
    }

    fun clear() {
        expenseList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ExpenseRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val expenseModel = expenseList[position]

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        dateFormat.timeZone = TimeZone.getTimeZone("IST");


        holder.binding.note.text = expenseModel?.note
        holder.binding.category.text = expenseModel?.category
        holder.binding.price.text = expenseModel?.amount.toString()
        holder.binding.date.text = dateFormat.format(expenseModel?.time?.let { Date(it) })
        if (expenseList[position]?.type == "Income") {
            holder.binding.apply {
                price.setTextColor(Color.rgb(1, 135, 134))
                currency.setTextColor(Color.rgb(1, 135, 134))
            }
        } else {
            holder.binding.apply {
                price.setTextColor(Color.rgb(183, 28, 28))
                currency.setTextColor(Color.rgb(183, 28, 28))
            }
        }

        holder.itemView.setOnClickListener {
            onItemsClick.onClick(expenseModel)
        }
    }
}