package com.example.myprojectapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
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
import java.time.LocalDate
import kotlinx.datetime.*
import java.util.Calendar


//리사이클러뷰 어댑터는 작성해둠



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

    //바인딩 처리
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimerEntryBinding.inflate(inflater)
        binding?.recShowToDo?.layoutManager = LinearLayoutManager(context)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnMoveTimer?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_timerFragment)
        }

        //버튼 삭제함. 추가적인 버튼 필요하면 이미지 버튼으로 새로 만들기

        //chart 관련 함수 호출
        chart = binding?.chartWeek
        setChart()

        //그냥 어레이로는 입력 안받은 칸 때문에 그래프 일~토 초기화 어려움
        //아래와 같이 적었는데 대신 해당하는 위치에 안찍히고 이상한 곳에 찎히는 문제 발생 수정하기
        //배열로 하려면 없는 값 0 채우든 해야할 듯
        viewModel.observeUser(viewModel.currentUserId?:"").observe(viewLifecycleOwner) { userData ->
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

        setMidnightAlarm()


        //리사이클러 뷰 코드
        val id = viewModel.currentUserId ?: "id 입력 안됨 "

        // ViewModel에서 사용자의 투두리스트 데이터를 관찰
        viewModel.observeUser(id).observe(viewLifecycleOwner) { userData ->
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

            //객체 자체를 번들로 넘기기 위해 parcelable 사용하여야함!
            checkedTodo?.let { todo ->
                // TodoList 객체를 Bundle에 넣어 전달
                val bundle = Bundle().apply {
                    putParcelable("todoItem", todo)
                }
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

    //밤 00시가 되면 유저데이터클래스의 배열에 템프 값 넣도록하는 함수! 알람리시버 클래스 작성해둠
    //인텐트 부분 다시 공부하기
    //00시에 해당하는 요일의 배열에 템프 값 들어가는지 확인
    private fun setMidnightAlarm() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("userId", viewModel.currentUserId)
            // putExtra("tempCycle", viewModel.tempCycle) // tempCycle는 AlarmReceiver에서 직접 접근하므로 여기서 넘기지 않아도 될 듯?
        }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }




    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }




}