package com.example.myprojectapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.ListTodoBinding

class TodoAdapter(val tempTodolist: Array<TodoList>): RecyclerView.Adapter<TodoAdapter.Holder>() {
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
    override fun getItemCount(): Int = tempTodolist.size


    // bind: 홀더가 몇 번째 아이템이라는 것을 알려줬을 떄 실제로 리스트 요소가 렌더링해주는 역할
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(tempTodolist[position])

    class Holder(private val binding: ListTodoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(tempTodolist: TodoList) {
            binding.txtTodo.text = tempTodolist.thing_Todo
            binding.txtDate.text = "${tempTodolist.year_Todo.toString()}년 ${tempTodolist.month_Todo.toString()}월 ${tempTodolist.day_Todo.toString()}일"
            // toast 띄우기
            // binding.root.setOnClickListener { Toast.makeText("") }
        }
    }
}