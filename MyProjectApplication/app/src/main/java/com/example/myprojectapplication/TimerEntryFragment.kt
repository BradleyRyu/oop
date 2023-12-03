package com.example.myprojectapplication

import TodayAdapter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentTimerEntryBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.datetime.*

class TimerEntryFragment : Fragment() {

    //바인딩, 뷰모델 등 변수 선언
    /*
    https://developer.android.com/topic/libraries/view-binding?hl=ko
    binding은 get()을 통해서 _binding이 널이 아닌 경우에만 접근할 수 있도록 : 델리게이트
        값을 가져오는 동작을 _binding에게 위임하는 형식
        만약 _binding이 null일 경우, nullpointexception으로 뷰가 파괴된 상태에서 접근하는 것 방지
     */
    private var _binding: FragmentTimerEntryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoViewModel by activityViewModels()
    private var todoList: MutableList<TodoList> = mutableListOf()

    private var chart: BarChart? = null //차트
    private val userId: String by lazy { viewModel.currentUserId ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerEntryBinding.inflate(inflater)
        _binding?.recShowToDo?.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateStudyCycles() //스터디 사이클 업데이트, date와 tempCycle 초기화

        // ViewModel에서 사용자의 투두리스트 데이터를 관찰
        viewModel.observeUser(userId).observe(viewLifecycleOwner) { userData ->
            // userData가 null이 아니면 투두리스트를 띄우기
            userData?.let {
                todoList = it.todo.toMutableList()
                binding.recShowToDo.adapter = TodayAdapter(getTodayTodoList(todoList))
            }
        }

        //chart 관련 함수 호출
        chart = binding.chartWeek
        setChart()
        drawChart()

        binding.btnMoveTimer.setOnClickListener {
            val adapter =
                (binding.recShowToDo.adapter as? TodayAdapter) ?: return@setOnClickListener

            // 체크된 항목이 없으면 토스트 메시지
            val selectedToDoList = adapter.todayList.firstOrNull { it.isSelected } ?: run {
                Toast.makeText(context, "체크된 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            findNavController().navigate(
                R.id.action_timerEntryFragment_to_timerFragment,
                bundleOf("todo" to selectedToDoList)
            )
        }
    }


    //오늘 날짜의 todolist 중 reamin Cycle이 0보다 큰 것만 띄우기
    private fun getTodayTodoList(todoList: MutableList<TodoList>): MutableList<TodoList> {
        val today = Clock.System.todayAt(TimeZone.currentSystemDefault())

        return todoList.filter {
            it.year_Todo == today.year &&
                    it.month_Todo == today.monthNumber &&
                    it.day_Todo == today.dayOfMonth &&
                    ((it.goalCycle ?: 0) - (it.achievedCycle ?: 0)) > 0 //remain Cycle이 0보다 큰 것만 띄우기
        }.toMutableList()

    }

    /*
    date = 빈문자열일 때 : 오늘 날짜로 date 업데이트, studyCycle 0으로 초기화
    date != 오늘 : 이미 지난 날인 경우. temp사이클을 studyCycles의 해당 요일에 넣고, date, temp사이클 초기화
    월요일: 인덱스0, 일요일 6
     */
    private fun updateStudyCycles() {
        val today = Clock.System.todayAt(TimeZone.currentSystemDefault())
        val todayString = today.toString()

        viewModel.observeTempCycles(userId).observe(viewLifecycleOwner, Observer { tempCycles ->
            viewModel.observeUser(userId).observe(viewLifecycleOwner) { userData ->
                when {
                    userData?.date == "" -> {
                        val studyCycles = MutableList(7) { 0 }
                        viewModel.updateStudyCycles(userId, studyCycles)
                        viewModel.updateDate(userId, todayString)
                    }

                    userData.date != todayString -> {
                        val studyCycles =
                            userData.studyCycles?.toMutableList() ?: MutableList(7) { 0 }
                        val dateOfUserData = LocalDate.parse(userData.date)
                        studyCycles[dateOfUserData.dayOfWeek.ordinal] = tempCycles
                        viewModel.updateStudyCycles(userId, studyCycles)
                        viewModel.updateTempCycles(userId, 0)
                        viewModel.updateDate(userId, todayString)
                    }

                    else -> {
                    }
                }
            }
        })
    }


    /*
    Chart
     */
    private fun setChart() {
        // 차트 설정 초기화
        chart?.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            // X축 설정
            xAxis.valueFormatter = IndexAxisValueFormatter(
                arrayOf("월", "화", "수", "목", "금", "토", "일")
            )
            xAxis.position = XAxis.XAxisPosition.BOTTOM // X축을 밑으로
            xAxis.setDrawGridLines(false) // X축 라인 끔

            // 왼쪽 Y축 설정
            axisLeft.granularity = 0.5f // Y축 간격 설정
            axisLeft.axisMinimum = 0.5f // Y축 최소값 설정
            axisLeft.axisMaximum = 12f // Y축 최대값 설정
            // 왼쪽만 사용함
            axisRight.isEnabled = false
        }
    }

    private fun addChart(studyCycles: List<Int>) {
        val entries = ArrayList<BarEntry>()
        val weekDays = listOf("월", "화", "수", "목", "금", "토", "일")
        val studyCyclesList = weekDays.zip(studyCycles)

        for ((index, pair) in studyCyclesList.withIndex()) {
            entries.add(BarEntry(index.toFloat(), pair.second.toFloat() / 2))
        }

        val dataSet = BarDataSet(entries, "공부한 시간")
        val barData = BarData(dataSet)
        chart?.data = barData
    }

    private fun drawChart() {
        viewModel.observeUser(userId).observe(viewLifecycleOwner) { userData ->
            // userData가 null이 아니면
            userData?.let {
                val studyCycles = it.studyCycles ?: MutableList(7) { 0 }
                addChart(studyCycles)
                setData()
            }
        }
    }

    private fun setData() {
        // 차트 데이터 갱신 함수
        chart?.invalidate()
    }
    /*
    Chart
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
