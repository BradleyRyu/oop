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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
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
    var chart:LineChart?=null //프라이빗 붙여야 하는지?

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

        /*
        //그냥 어레이로는 입력 안받은 칸 때문에 그래프 일~토 초기화 어려움
        //아래와 같이 적었는데 대신 해당하는 위치에 안찍히고 이상한 곳에 찎히는 문제 발생 수정하기
        //배열로 하려면 없는 값 0 채우든 해야할 듯
        viewModel.observeUser(userId).observe(viewLifecycleOwner) { userData ->
            // userData가 null이 아니면 투두리스트를 띄우기
            userData?.let {
                // 투두리스트를 UI에 띄우는 코드
                todoList = it.todo.toMutableList()
                todayList = getTodayTodoList(todoList)
                binding?.recShowToDo?.adapter = TodayAdapter(todayList)

                val weekDays = listOf("일", "월", "화", "수", "목", "금", "토")
                val studyCyclesMap = weekDays.associateWith { 0 }.toMutableMap()
                it.studyCycles.forEach { (day, value) ->
                    studyCyclesMap[day] = value
                }
                val studyCyclesList = studyCyclesMap.entries.map { entry -> Pair(entry.key, entry.value) }

                addChart(studyCyclesList)
                setData()
            }
        }

         */

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
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("일", "월", "화", "수", "목", "금", "토")) // 일~월로 x축 표시되도록 format 변경
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

    private fun addChart(studyCycles: List<Pair<String, Int>>) {
        val entries = ArrayList<Entry>()
        val hours = studyCycles.map { it.second.toFloat() }

        //데이터 포인트 차례로 추가
        for (i in hours.indices) {
            entries.add(Entry(i.toFloat(), hours[i]))// 각 요일 0~6(실수), 해당하는 시간 add
        }
        val dataSet = LineDataSet(entries, "공부한 시간") // 데이터 셋 생성
        val lineDataSets: ArrayList<ILineDataSet> = ArrayList()
        lineDataSets.add(dataSet)

        val lineData = LineData(lineDataSets) // 라인 데이터 생성
        chart?.data = lineData // 차트에 데이터 설정
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


   //업데이트는 되는데 첫번째에만 이상하게 작동함.
    //처음 만들고 돌리면 temp사이클을 바로 0으로 만들고 study사이클에 넣음
    //초기화 문제? >> 데이트와 스터디사이클 배열 미리 준비하기
    //지금보니 무조건 쓰는 문제 발생하는 것 같기도?
    //처음에는 날짜 없으니까 무조건 들어가는 듯
    //데이트 널일 때, 널은 아닌데 투데이도 아닐때, 오늘일 때 >> 이렇게 나눠서 각각 초기화, 업데이트, 아무것도안함

    fun updateStudyCycles() {
        val today = Clock.System.todayAt(TimeZone.currentSystemDefault())
        val todayString = today.toString()

        viewModel.observeTempCycles(userId).observe(viewLifecycleOwner, Observer { tempCycles ->
            viewModel.observeUser(userId).observe(viewLifecycleOwner) { userData ->
                userData?.let {
                    if (it.date != todayString) {
                        val studyCycles = it.studyCycles?.toMutableList() ?: MutableList(7) { 0 }
                        studyCycles[today.dayOfWeek.ordinal] = tempCycles
                        viewModel.updateStudyCycles(userId, studyCycles)
                        viewModel.updateTempCycles(userId, 0)
                        viewModel.updateDate(userId, todayString)
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