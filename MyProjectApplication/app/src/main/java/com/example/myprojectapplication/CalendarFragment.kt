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

    // 현재 프래그먼트에 대한 바인딩 설정
    var binding: FragmentCalendarBinding? = null

    // TodoList를 불러올 TodoViewModel 초기화
    val viewModel: TodoViewModel by activityViewModels()

    // id같은 경우 LoginFragment에서 입력된 사용자의 ID를 viewModel에서 불러와야하고
    // 그 전에는 초기화하지 않기 때문에 by lazy로 선언
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

        // 오늘에 해당하는 TodoList 출력
        displayTodoListForDate(todayDate)

        //recyclerview의 레이아웃 설정
        binding?.recTodoOfTheDay?.layoutManager = LinearLayoutManager(context)

        // 사용자가 지정한 날짜가 바뀌는 이벤트마다 실행하는 코드
        binding?.calendar?.setOnDateChangeListener { _, yyyy, mm, dd ->

            // 사용자가 선택한 날짜를 담는 자료구조
            val selectedDate = LocalDate.of(yyyy, mm + 1, dd)

            // 선택한 날짜 출력
            binding?.txtSelectedDate?.text = "${selectedDate.year}년 ${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일"

            // 선택한 날짜에 해당하는 TodoList 출력
            displayTodoListForDate(selectedDate)

        }

        // 바인딩 리턴
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    // 사용자에 ID에 따라 선택한 날짜에 해당하는 TodoList를 출력하는 함수. 앞서 만든 LocatDate 자료구조를 인자로 받음
    private fun displayTodoListForDate(date: LocalDate) {
        viewModel.observeUser(id).observe(viewLifecycleOwner) { userID ->
            //viewModel이 user의 id를 관찰한 결과, 널이 아닐 시 실행
            userID?.let {
                // user의 TodoList를 MutableList로 받아와서
                val todoList = it.todo.toMutableList()
                // 선택한 날짜에 해당하는 TodoList만 걸러 가져온 뒤
                val todoListOnSelectedDay = getTodoListForDate(todoList, date)
                // TodoAdapter의 재사용으로 선택된 날의 TodoList를 어댑터에 넣어줌
                val todoAdapter = TodoAdapter(todoListOnSelectedDay)
                binding?.recTodoOfTheDay?.adapter = todoAdapter
            }
        }
    }

    // 선택한 날짜에 해당하는 TodoList를 반환하는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodoListForDate(todoList: MutableList<TodoList>, selectedDate: LocalDate): MutableList<TodoList> {
        // 인자로 받은 TodoList를 선택한 연 월 일이 같은 경우로 필터링하여 MutableList로 반환
        return todoList.filter {
            it.year_Todo == selectedDate.year &&
                    it.month_Todo == selectedDate.monthValue &&
                    it.day_Todo == selectedDate.dayOfMonth
        }.toMutableList()
    }
}