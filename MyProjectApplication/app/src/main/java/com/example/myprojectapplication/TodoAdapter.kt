package com.example.myprojectapplication

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.ListTodoBinding

class TodoAdapter(val todoList: MutableList<TodoList>): RecyclerView.Adapter<TodoAdapter.Holder>() {
                                                                // adapter가 하는 일은 UI를 렌더링할 떄 필요한 내용들을 넘겨주는 것
                                                                // 어떤 데이터를 기반으로 할 것인지 알고 있어야함
                                                                // 모델에 해당하는 data structure을 넘겨줘야 함

    // view holder = 리사이클러뷰에 들어갈 아이템 한 칸
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //parent: holder를 가진 view group .context = MainActicity
        val binding = ListTodoBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    // 이 뷰 안에 들어가는 아에팀의 개수가 어떻게 되는가?
    override fun getItemCount(): Int = todoList.size

    // bind: 홀더가 몇 번째 아이템이라는 것을 알려줬을 떄 실제로 리스트 요소가 렌더링해주는 역할
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(todoList[position])


    class Holder(private val binding: ListTodoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(todolist: TodoList) {

            // todoList에 띄울 항목들
            binding.txtTodo.text = todolist.thing_Todo
            binding.txtDate.text = "${todolist.year_Todo.toString()}년 ${todolist.month_Todo.toString()}월 ${todolist.day_Todo.toString()}일"
            binding.txtGoalCycle.text = todolist.goalCycle.toString()
            binding.txtDoneCycle.text = todolist.achievedCycle.toString()

            // toString으로 적용 시 색깔이 제대로 반영되지 않는 오류 발견
            // 목표 사이클에 달성되지 않은 경우 -> 빨간글씨 , 달성된 경우 -> 파란 글씨
            if( (binding.txtDoneCycle.text as String) < (binding.txtGoalCycle.text as String)) {
                binding.txtDoneCycle.setTextColor(Color.parseColor("red"))
            } else {
                binding.txtDoneCycle.setTextColor(Color.parseColor("blue"))
            }

        }
    }

    // 항목에서 제거
    fun removeList(position: Int) {
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }
}

