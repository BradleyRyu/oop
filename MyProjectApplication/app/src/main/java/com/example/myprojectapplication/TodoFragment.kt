package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentTodoBinding

class TodoFragment : Fragment() {

    private var binding: FragmentTodoBinding? = null

    /*
    private var param1: String? = null
    private var param2: String? = null

     */

    private var tempTodolist: Array<TodoList> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* 추후 스피너를 통해 구현되면 값을 넘겨줄 때 사용할 값
        var spinner_year = arrayOf("2023년", "2024년", "2025년")
        var spinner_month = arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        var spinner_day = arrayOf("1일", "2일", "3일", "4일", "5일")
        //test
        val spinner_year = arrayOf("2023년", "2024년", "2025년")
        val spinner_month = DateFormatSymbols().months
        val spinner_day = DateFormatSymbols().shortWeekdays
         */
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false)
        //layoutmanager: 리사이클러뷰에 어떻게 아이템을 쌓을 것인가, 어떤 형식으로 보여줄 것이냐
        binding?.recTodo?.layoutManager = LinearLayoutManager(context)

        // 기존 리스트를 정렬하고 그 값을 정렬된 리스트로 어댑터 갱신
        binding?.recTodo?.adapter = TodoAdapter(sortTodoList())

        // 할 일을 추가하는 버튼을 클릭하면 생기는 이벤트.
        binding?.btnAdd?.setOnClickListener {
            // 추가할 todolist에 대한 정보를 받고 기존 리스트에 추가함
            // AddToList fragment로부터 읽어온 값을 추가함 (할 일과 날짜를 받아옴) -> 추후 구현
            // 상단에 있는 텍스트 입력창을 통해 할 일과 언제 할 일인지를 읽고 값을 추가함
            // input year, month, day에 입력한 값을 정수로 변환하여 함수를 통해 입력 받음

            val whattodo: String = binding?.inputWhattodo?.text.toString()
            val strInputYear: String = binding?.inputYear?.text.toString()
            val strInputMonth: String = binding?.inputMonth?.text.toString()
            val strInputDay: String = binding?.inputDay?.text.toString()

            // 입력받은 문자열의 유효성을 검사하는 과정

            if( isValidString(whattodo, strInputYear, strInputMonth, strInputDay) ) {
                addList( whattodo,
                    strInputYear.let { it1 -> Integer.parseInt(it1) },
                    strInputMonth.let { it1 -> Integer.parseInt(it1) },
                    strInputDay.let { it1 -> Integer.parseInt(it1) },
                    false
                )
            }

            // 어댑터에게 아이템이 삽입되었다는 것을 알려줌 (notifyDataSetChanged를 사용하려 했으나 안드로이드 공식 문서에서 특정 작업에 대한 notify를 쓸 것을 권장하였음.
            // 입력된 리스트를 포함하여 다시 todo리스트 정렬하고

            binding?.recTodo?.adapter?.notifyItemInserted(sortTodoList().size)
            // 갱신된 리스트로 어댑터를 갱신
            binding?.recTodo?.adapter = TodoAdapter(sortTodoList())

            // 추가하기 버튼을 누를 시, 칸을 비움
            makeTextBoxesBlank()
        }
        // Inflate the layout for this fragment

        return binding?.root
    }

    /* 추후 구현 고려
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //AddToList fragment 로 이동
        binding?.btnAddList?.setOnClickListener {
            findNavController().navigate(R.id.action_todoFragment_to_addToListFragment)
        }
    }
     */

    // todolist array를 연 월 일 순으로 정렬하는 함수
    fun sortTodoList(): Array<TodoList> {
        val sortedTodolist = tempTodolist.sortedWith(
            compareBy(
                { it.year_Todo },
                { it.month_Todo },
                { it.day_Todo }
            )
        ).toTypedArray()
        return sortedTodolist
    }

    /*, whentodo_year, whentodo_month, whentodo_day*/

    //addList로 넘어 온 값들의 유효성 검사 함수
    private fun addList(
        whattodo: String?,
        whentodo_year: Int?,
        whentodo_month: Int?,
        whentodo_day: Int?,
        donetodo: Boolean
    ): Boolean {
        return if( isValidDate(whentodo_year, whentodo_month, whentodo_day) ) {
            tempTodolist += TodoList(whattodo, whentodo_year, whentodo_month, whentodo_day, donetodo)
            true
        } else {
            false
        }
    }
    //사용자 편의를 위해 할 일에 적은 것은 남겨둠
    fun makeTextBoxesBlank() {
        binding?.inputWhattodo?.setText("")
        binding?.inputYear?.setText("")
        binding?.inputMonth?.setText("")
        binding?.inputDay?.setText("")
    }

    // 입력한 날짜 값이 유효한 값인지 검사함.
    fun isValidDate(year: Int?, month: Int?, day: Int?): Boolean {
        if (year != null && month != null && day != null) {
            if (year >= 2023 && (month in 1..12)) {
                if (month % 2 == 1 || month == 8) {
                    if (day in 1..31) return true
                } else if (month % 2 == 0 && month != 2) {
                    if (day in 1..30) return true
                } else {
                    if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                        if (day in 1..29) return true
                    } else {
                        if (day in 1..28) return true
                    }
                }
            }
        }
        Toast.makeText(binding?.root?.context, "날짜가 잘못 입력되었으니 확인 바랍니다.", Toast.LENGTH_SHORT).show()
        return false
    }

    // 무엇을 할지 / 언제 할지에 대한 값이 빈 문자열이거나 null이라면
    fun isValidString(whattodo: String?, whenYear: String?, whenMonth: String?, whenDay: String?): Boolean {
        if ( whattodo.isNullOrBlank() || whenYear.isNullOrBlank() || whenMonth.isNullOrBlank() || whenDay.isNullOrBlank() ) {
            Toast.makeText(binding?.root?.context, "모든 칸에 입력 바랍니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}