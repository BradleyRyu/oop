package com.example.myprojectapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.ListTodayBinding
import com.example.myprojectapplication.databinding.ListTodoBinding

class TodayAdapter(val todayList: MutableList<TodoList>) : RecyclerView.Adapter<TodayAdapter.Holder>() {

    class Holder(private val binding: ListTodayBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(today: TodoList) {
            binding.txtToday.text = today.thing_Todo
            //binding.txtTime.text = today. >> 이 부분은 아직
            binding.chkStudy.isChecked = today.isChecked

            binding.chkStudy.setOnCheckedChangeListener { _, isChecked ->
                today.isChecked = isChecked
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListTodayBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun getItemCount() = todayList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(todayList[position])
    }


}