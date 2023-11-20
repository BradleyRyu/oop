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
        binding?.txtSelectedDate?.text = "${todayYear}년 ${todayMonth}월 ${todayDay}일"
        binding?.calendar?.setOnDateChangeListener { _, yyyy, mm, dd ->
                // to_do list와 연계를 위해, 선택된 날짜를 변수에 저장하여 활용함
            val selectedYear: Int = yyyy
            val selectedMonth: Int = mm + 1
            val selectedDay: Int = dd
            binding?.txtSelectedDate?.text = "${selectedYear}년 ${selectedMonth}월 ${selectedDay}일"
            // 선택된 날짜에 해당하는 Todolist 목록을 캘린더로 불러오기
            binding?.recTodoOfTheDay?.layoutManager = LinearLayoutManager(context)
        }

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
        //친구 List로 이동
        binding?.btnFriendslist?.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_friendsListFragment)
        }
        //타이머로 이동
        binding?.btnTimer?.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_timerEntryFragment)
        }
    }
}
