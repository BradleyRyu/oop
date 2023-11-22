package com.example.myprojectapplication

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentCalendarBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

class CalendarFragment : Fragment() {

    var binding: FragmentCalendarBinding? = null
    val viewModel: TodoViewModel by activityViewModels()
    val id by lazy {
        viewModel.currentUserId ?: "ID 입력 안 됨"
    }

    // fragment가 생성될 당시 실행될 코드 ( 화면 구성 )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // oncreate에서 화면 구성이 끝난 이후 실행될 코드
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding 을 얻어내면 -> xml에 들어가있는 여러 컴포넌트들을 binding의 컴포넌트로 접근할 수 있음
        binding = FragmentCalendarBinding.inflate(inflater)
        // 얻어온 calendar의 선택된 날짜에 따라 해당 연 월 일을 출력함.
        // 어플 실행 시 날짜를 클릭하지 않으면 오늘 날짜가 뜨지 않는 문제로 추가적인 코드
        val calendar: Calendar = Calendar.getInstance()
        val todayYear: Int = calendar.get(Calendar.YEAR)
        val todayMonth: Int = calendar.get(Calendar.MONTH) + 1
        val todayDay: Int = calendar.get(Calendar.DATE)
        // 오늘 날짜를 담는 자료구조
        val todayDate = LocalDate.of(todayYear, todayMonth, todayDay)
        // 오늘의 날짜 출력
        binding?.txtSelectedDate?.text = "${todayDate.year}년 ${todayDate.monthValue}월 ${todayDate.dayOfMonth}일"
        // 오늘에 해당하는 Todo-List 출력
        displayTodoListForDate(todayDate)
        binding?.recTodoOfTheDay?.layoutManager = LinearLayoutManager(context)

        // 사용자가 지정한 날짜가 바뀌는 이벤트마다 실행하는 코드
        binding?.calendar?.setOnDateChangeListener { _, yyyy, mm, dd ->
            // 사용자가 선택한 날짜를 담는 자료구조
            val selectedDate = LocalDate.of(yyyy, mm + 1, dd)
            // 선택한 날짜 출력
            binding?.txtSelectedDate?.text = "${yyyy}년 ${mm + 1}월 ${dd}일"
            // 선택한 날짜에 해당하는 Todo-List 출력
            displayTodoListForDate(selectedDate)
        }

        // Inflate the layout for this fragment
        return binding?.root
    }

    //버튼 클릭 시 이동 ( Navigation )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodoListForDate(todoList: MutableList<TodoList>, selectedDate: LocalDate): MutableList<TodoList> {
        return todoList.filter {
            it.year_Todo == selectedDate.year &&
                    it.month_Todo == selectedDate.monthValue &&
                    it.day_Todo == selectedDate.dayOfMonth
        }.toMutableList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayTodoListForDate(date: LocalDate) {
        viewModel.observeUser(id).observe(viewLifecycleOwner) { userID ->
            userID?.let {
                val todoList = it.todo.toMutableList()
                val todoListOnSelectedDay = getTodoListForDate(todoList, date)
                val todoAdapter = TodoAdapter(todoListOnSelectedDay)
                binding?.recTodoOfTheDay?.adapter = todoAdapter
            }
        }
    }


}