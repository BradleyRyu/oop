package com.example.myprojectapplication

import TodayAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentTimerEntryBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.datetime.*

/* Chart xml code
        <com.github.mikephil.charting.charts.LineChart
            android:id="@+id/chart_week"
            android:layout_width="match_parent"
            android:layout_height="200dp" />
 */

class TimerEntryFragment : Fragment() {

    var binding:FragmentTimerEntryBinding? = null
    var chart: BarChart?=null //프라이빗 붙여야 하는지?

    val viewModel: TodoViewModel by activityViewModels()
    private var todoList: MutableList<TodoList> = mutableListOf()
    private var todayList: MutableList<TodoList> = mutableListOf()

    val userId : String by lazy{
        viewModel.currentUserId ?: ""
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimerEntryBinding.inflate(inflater)
        binding?.recShowToDo?.layoutManager = LinearLayoutManager(context)
        return binding?.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateStudyCycles()

        binding?.btnMoveTimer?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_timerFragment)
        }

        //chart 관련 함수 호출
        chart = binding?.chartWeek
        setChart()
        drawChart()


        //리사이클러 뷰 코드

        // ViewModel에서 사용자의 투두리스트 데이터를 관찰
        viewModel.observeUser(userId).observe(viewLifecycleOwner) { userData ->
            // userData가 null이 아니면 투두리스트를 띄우기
            userData?.let {
                // 투두리스트를 UI에 띄우는 코드
                todoList = it.todo.toMutableList()
                todayList = getTodayTodoList(todoList)
                binding?.recShowToDo?.adapter = TodayAdapter(todayList)
            }
        }

        binding?.btnMoveTimer?.setOnClickListener {
            // 체크된 항목 선택
            val checkedTodo = todayList.find { it.isChecked }

            checkedTodo?.let { todo ->
                // TodoList 객체의 인덱스를 찾아 번들로 넘김
                val todoIndex = todayList.indexOf(todo)
                val bundle = bundleOf("todoIndex" to todoIndex)
                findNavController().navigate(R.id.action_timerEntryFragment_to_timerFragment, bundle)
            } ?: run {
                // 체크된 항목이 없으면 토스트 메시지
                Toast.makeText(context, "체크된 항목이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }




    }







    // 아래로 Chart 설정
    private fun setChart() {
        // 차트 설정 초기화
        chart?.apply {
            // 차트 설명
            description.isEnabled = false
            // 사용자화 가능 여부 >> 일단 true로 해둠 용도에 맞춰 수정하기
            setTouchEnabled(true) // 터치
            isDragEnabled = true // 드래그
            setScaleEnabled(true) // 스케일링 가능
            setPinchZoom(true) // 확대/축소 가능

            // X축 설정
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("월", "화", "수", "목", "금", "토", "일")) // 일~월로 x축 표시되도록 format 변경
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
            entries.add(BarEntry(index.toFloat(), pair.second.toFloat()/2))
        }

        val dataSet = BarDataSet(entries, "공부한 시간")
        val barData = BarData(dataSet)
        chart?.data = barData
    }

    private fun drawChart() {
        viewModel.observeUser(userId).observe(viewLifecycleOwner) { userData ->
            // userData가 null이 아니면 투두리스트를 띄우기
            userData?.let {
                val studyCycles = it.studyCycles ?: MutableList(7) { 0 }
                addChart(studyCycles)
                setData()
            }
        }
    }


    //차트 데이터 변경할 떄 호출해서 갱신하는 용도
    private fun setData() {
        // 차트 데이터 갱신 함수
        chart?.invalidate() // 차트 데이터 변경 시 호출
    }

    private fun getTodayTodoList(todoList: MutableList<TodoList>): MutableList<TodoList> {
        val today = Clock.System.todayAt(TimeZone.currentSystemDefault())

        return todoList.filter {
            it.year_Todo == today.year &&
                    it.month_Todo == today.monthNumber &&
                    it.day_Todo == today.dayOfMonth
        }.toMutableList()

    }


    //date = 빈문자열일 때 : 오늘 날짜로 date 업데이트, studyCycle 0으로 초기화
    //date != 오늘 : 이미 지난 날인 경우. temp사이클을 studyCycles의 해당 요일에 넣고, date, temp사이클 초기화
    //else : 둘 다 아닐 경우는 없음
    //월요일: 인덱스0, 일요일 6
    fun updateStudyCycles() {
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
                        val studyCycles = userData.studyCycles?.toMutableList() ?: MutableList(7) { 0 }
                        val dateOfUserData = LocalDate.parse(userData.date)
                        studyCycles[dateOfUserData.dayOfWeek.ordinal] = tempCycles
                        viewModel.updateStudyCycles(userId, studyCycles)
                        viewModel.updateTempCycles(userId, 0)
                        viewModel.updateDate(userId, todayString)
                    }
                    else -> {
                        // date와 오늘 날짜가 같을 때는 아무 일도 하지 않는다.
                    }
                }
            }
        })
    }













    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }




}