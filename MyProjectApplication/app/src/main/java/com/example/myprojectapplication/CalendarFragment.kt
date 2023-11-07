package com.example.myprojectapplication

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    var binding: FragmentCalendarBinding? = null

    // fragment가 생성될 당시 실행될 코드 ( 화면 구성 )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // oncreate에서 화면 구성이 끝난 이후 실행될 코드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding 을 얻어내면 -> xml에 들어가있는 여러 컴포넌트들을 binding의 컴포넌트로 접근할 수 있음
        binding = FragmentCalendarBinding.inflate(inflater)
        // 얻어온 calendar의 선택된 날짜에 따라 해당 연 월 일을 출력함.
        // 어플 실행 시 날짜를 클릭하지 않으면 오늘 날짜가 뜨지 않는 문제로 추가적인 코드가 필요함
        binding?.calendar?.setOnDateChangeListener { _, yyyy, mm, dd ->
            // to_do list와 연계를 위해, 선택된 날짜를 변수에 저장하여 활용함
            val selectedYear: Int = yyyy
            val selectedMonth: Int = mm + 1
            val selectedDay: Int = dd
            binding?.txtSelectedDate?.text = "${selectedYear}년 ${selectedMonth}월 ${selectedDay}일"
            // 선택된 날짜에 해당하는 Todolist 목록을 캘린더로 불러오기
            binding?.recTodoOfTheDay?.layoutManager = LinearLayoutManager(context)
        }

        /* 앱 실행 시 오늘의 날짜를 어떻게 구현할지 ...?
        var selectYear: Int = LocalDate.now().year
        var selectedMonth: Int = LocalDate.now().monthValue
        var selectedDay: Int = LocalDate.now().dayOfMonth
         */

        // Inflate the layout for this fragment
        return binding?.root
    }

    //버튼 클릭 시 이동 ( Navigation )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //To-do List로 이동
        binding?.btnToTodo?.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_todoFragment)
        }
        //유저 ID로 이동
        binding?.btnUserId?.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_userFragment)
        }

        //timer로 이동 by 김상일
        binding?.btnTimer?.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_timerFragment)
        }
    }
}
