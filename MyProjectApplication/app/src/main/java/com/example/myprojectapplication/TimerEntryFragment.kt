package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.myprojectapplication.databinding.FragmentTimerEntryBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


/* Chart xml code
        <com.github.mikephil.charting.charts.LineChart
            android:id="@+id/chart_week"
            android:layout_width="match_parent"
            android:layout_height="200dp" />
 */
class TimerEntryFragment : Fragment() {

    var binding:FragmentTimerEntryBinding? = null
    var chart:LineChart?=null

    //바인딩 처리
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimerEntryBinding.inflate(inflater)

        // Inflate the layout for this fragment
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnMoveCalender?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_calenderFragment)
        }

        binding?.btnMoveFriends?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_friendsListFragment)
        }

        binding?.btnMoveTimer?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_timerFragment)
        }

        binding?.btnMoveTodo?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_addToListFragment)
        }

        //chart 관련 함수 호출
        chart = binding?.chartWeek
        setChart()
        addChart()
        setData()
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

    /*
    차트에 데이터 추가 일단 임시 값 넣음
    공부시간 기록하여서 자동으로 추가되도록 추후에 설정하면 됨.
    추후 설정시 for문 형태가 아니라
    entries.add(Entry(0f, 8f))    // "월", 8
    위와 같은 형태로 하루 지날 떄 마다 덮어쓰며 업데이트 되도록 설정하면 될 듯
     */

    //수치 > 전부 f 사용해야함
    private fun addChart() {
        val entries = ArrayList<Entry>()
        val hours = arrayOf(8f, 6f, 7f, 8.5f, 7.5f, 6.5f, 9f) // 임의의 시간 float으로 설정!

        //데이터 포인트 차례로 추가
        for (i in hours.indices) {
            entries.add(Entry(i.toFloat(), hours[i]))// 각 요일 0~6(실수), 해당하는 시간 add
        }
        //아래 val dataset, lineDataSets, lineData 과정 잘 이해 안감
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


    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}