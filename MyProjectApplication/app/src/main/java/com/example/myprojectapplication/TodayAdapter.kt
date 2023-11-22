package com.example.myprojectapplication

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.ListTodayBinding
import com.example.myprojectapplication.databinding.ListTodoBinding

class TodayAdapter(val todayList: MutableList<TodoList>) : RecyclerView.Adapter<TodayAdapter.Holder>() {

    private var checked = -1 //체크 1개만 하기 위해 생성한 변수

    class Holder(internal val binding: ListTodayBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(today: TodoList) {
            binding.txtToday.text = today.thing_Todo
            //binding.txtTime.text = today. >> 이 부분은 아직 투두리스트 구현에 따라

            binding.chkStudy.setOnCheckedChangeListener(null) // listener를 잠시 해제 > 1개만 체크 가능하게 하기 위해

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

    override fun onBindViewHolder(holder: Holder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(todayList[position])

        // 체크박스 클릭 리스너 설정
        holder.binding.chkStudy.setOnClickListener {
            if (checked != position) {
                if (checked != -1) {
                    // 이전에 체크된 항목 체크 해제
                    todayList[checked].isChecked = false
                }
                // 현재 아이템 체크
                todayList[position].isChecked = true
                checked = position
            } else {
                // 이미 체크된 아이템을 다시 클릭하면 체크 해제
                todayList[position].isChecked = false
                checked = -1
            }
            notifyDataSetChanged() // 데이터 변경 통지
        }
    }


}