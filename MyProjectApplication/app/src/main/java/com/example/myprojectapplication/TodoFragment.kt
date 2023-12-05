package com.example.myprojectapplication

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.FragmentTodoBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel
import java.time.DateTimeException
import java.time.LocalDate

class TodoFragment : Fragment() {

    // TodoFragment에 대한 바인딩 설정
    private var binding: FragmentTodoBinding? = null

    // todoList를 empty MutableList로 초기화
    private var todoList: MutableList<TodoList> = mutableListOf()

    // 사용자의 정보를 가져올 TodoViewModel 초기화
    private val viewModel: TodoViewModel by activityViewModels()

    // id같은 경우 LoginFragment에서 입력된 사용자의 ID를 viewModel에서 불러와야하고
    // 그 전에는 초기화하지 않기 때문에 by lazy로 선언
    private val id by lazy {
        viewModel.currentUserId ?: "ID 입력 안 됨"
    }

    // ItemTouchHelper를 선언
    // This is a utility class to add swipe to dismiss and drag & drop support to RecyclerView.
    // ItemTouchHelper는 리사이클러뷰에 대해 스와이프로 삭제 및 드래그 앤 드랍을 추가하는 유틸리티 클래스
    // swipe가 일어나기 전까지는 불릴 일이 없으므로(초기화되지 않으므로), by lazy로 선언
    private val itemTouchHelper by lazy {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, // up과 down쪽 스와이프는 사용하지 않을 것이므로, 해당 파라미터는 0
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            // 현재 어플에서는 사용하지 않는 함수이지만, 추상 기본 클래스 멤버 구현을 위해 구현
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // 스와이프 시 해당 아이템 삭제하는 함수
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 현재 viewholder의 어댑터 위치를 가져옴
                val position = viewHolder.adapterPosition
                // 만약 recTodo의 어댑터가 TodoAdapter의 인스턴스(메모리에 올라간 객체)라면, TodoAdapter의 removeList를 사용해 해당 위치의 아이템을 제거함.
                (binding?.recTodo?.adapter as? TodoAdapter)?.removeList(position)
                viewModel.removeTodoItem(id, position)
                floatingToast("삭제되었습니다.")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TodoFragment에 대한 binding 설정
        binding = FragmentTodoBinding.inflate(inflater, container, false)

        // layoutmanager: 리사이클러뷰에 어떻게 아이템을 쌓을 것인가, 어떤 형식으로 보여줄 것이냐
        // LinearLayout으로 투두리스트 리사이클러뷰를 보여줌
        binding?.recTodo?.layoutManager = LinearLayoutManager(context)

        // ItemTouchHelper를 RecyclerView에 연결 ( 스와이프를 통한 리스트 삭제를 위함 )
        itemTouchHelper.attachToRecyclerView(binding?.recTodo)

        // id = arguments?.getString("id")? : "id 입력 안됨 " 아이디 번들이 아니라 뷰모델로 불러오기
        // ViewModel에서 사용자의 투두리스트 데이터를 관찰
        viewModel.observeUser(id).observe(viewLifecycleOwner) { userID ->
            // userID가 null이 아니면 정렬된 리스트를 어댑터에 전달
            userID?.let {
                // 투두리스트를 UI에 띄우는 코드
                todoList = it.todo.toMutableList()
                binding?.recTodo?.adapter = TodoAdapter(sortTodoList(todoList))
            }
        }

        // 목표 사이클 수 설정을 위한 스피너( Dropdown Menu ) 설정부분
        val goalCycle = listOf("목표 사이클 수를 설정하세요.", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        //context가 null이 될 수 있는 경우
        // onCreate 이전에 context가 불리는 경우

        val spinnerAdapter =
            binding?.root?.context?.let {
                ArrayAdapter(it, android.R.layout.simple_list_item_1, goalCycle)
            }
        binding?.spnCycle?.adapter = spinnerAdapter

        // 할 일을 추가하는 버튼을 클릭하면 생기는 이벤트.
        binding?.btnAdd?.setOnClickListener {
            // 추가할 todolist에 대한 정보를 받고 기존 리스트에 추가함
            // 상단에 있는 텍스트 입력창을 통해 할 일과 언제 할 일인지를 읽고 값을 추가함
            // editText 박스 내부 input year, month, day에 입력한 값을 정수로 변환하여 함수를 통해 입력 받음

            // 사용자가 할 일
            val whatToDo: String = binding?.inputWhattodo?.text.toString()

            // 사용자가 일을 할 연도
            val inputYear: String = binding?.inputYear?.text.toString()

            // 사용자가 일을 할 월
            val inputMonth: String = binding?.inputMonth?.text.toString()

            // 사용자가 일을 할 날짜
            val inputDay: String = binding?.inputDay?.text.toString()

            // 사용자가 목표하는 사이클 수를 스피너로부터 얻어옴
            val inputCycle: String = binding?.spnCycle?.selectedItem.toString()


            // 입력받은 문자열의 유효성을 검사하는 과정. Integer.parseInt() 함수에서 널값이나 빈 칸인 값이 오면 프로그램이 오류로 종료되어 toInt로 형 변환
            if( isValidString(whatToDo, inputYear, inputMonth, inputDay, inputCycle) ) {
                addList( whatToDo,
                    inputYear.toInt(),
                    inputMonth.toInt(),
                    inputDay.toInt(),
                    inputCycle.toInt(),
                    0
                )
                // 추가하기 버튼을 누를 시, 칸을 비움
                makeTextBoxesBlank()
            }
        }

        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    // todolist array를 연 월 일 순으로 정렬하는 함수
    private fun sortTodoList(todoList: MutableList<TodoList>): MutableList<TodoList> {
        return todoList.sortedWith(
            compareBy(
                { it.year_Todo },
                { it.month_Todo },
                { it.day_Todo }
            )
        ).toMutableList()
    }

    //addList로 넘어 온 값들의 유효성 검사 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addList(
        whattodo: String?,
        whentodo_year: Int?,
        whentodo_month: Int?,
        whentodo_day: Int?,
        goalCycle: Int?,
        achievedCycle: Int?
    ): Boolean {
        return if( isValidDate(whentodo_year, whentodo_month, whentodo_day) ) {
            todoList.add(TodoList(whattodo, whentodo_year, whentodo_month, whentodo_day, goalCycle, achievedCycle))
            // 입력된 리스트를 포함하여 다시 todo리스트 정렬하고
            todoList = sortTodoList(todoList)

            // 어댑터에게 아이템이 삽입되었다는 것을 알려줌
            // (notifyDataSetChanged를 사용하려 했으나 안드로이드 공식 문서에서 특정 작업에 대한 notify를 쓸 것을 권장하였음.
            binding?.recTodo?.adapter?.notifyItemInserted(todoList.size)

            // 정렬된 리스트로 어댑터 갱신
            binding?.recTodo?.adapter = TodoAdapter(todoList)

            // 정렬된 리스트를 firebase에 업데이트
            viewModel.updateTodoItem(id, todoList)

            // 추가된 경우 토스트 메세지를 띄움
            floatingToast("추가되었습니다.")

            // true값을 return하여 리스트에 추가를 허가함
            true

        } else {
            // 입력값 유효성 검사에서 통과하지 않으면 false를 반환하여 아무런 작업도 하지 않음
            false
        }
    }

    // 추가하기 버튼을 눌렀을 때, 모든 텍스트박스를 비움
    private fun makeTextBoxesBlank() {
        binding?.inputWhattodo?.setText("")
        binding?.inputYear?.setText("")
        binding?.inputMonth?.setText("")
        binding?.inputDay?.setText("")
        binding?.spnCycle?.setSelection(0)
    }

    // 입력한 날짜 값이 유효한 값인지 검사함. ( 각 월의 최대 일수 및 연도에 따른 윤달 체크 )
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isValidDate(year: Int?, month: Int?, day: Int?): Boolean {

        // 오늘의 날짜를 저장하는 변수를 선언함 (이후 사용자가 오늘 이전의 날짜를 입력했을 경우, 비교를 위한 대비)
        val currentDate: LocalDate = LocalDate.now()

        // 사용자가 기입한 날짜를 LocalDate 변수로 생성하여 비교를 용이하게 함
        // 또한 넘어오는 값이 nullable한 객체들이기 때문에, null로 넘어올 시 현재 날짜로 값을 저장하도록 세탕함
        val inputDate: LocalDate = try {

            // 넘어온 year, month, day값이 null 이라면 오늘 날짜로 설정함.
            LocalDate.of(year ?: currentDate.year, month ?: currentDate.monthValue, day ?: currentDate.dayOfMonth)

        } catch (e: DateTimeException) {

            // DateTimeException -> 유효한 날짜 값이 아닐 경우의 에러
            // 사용자가 입력한 값이 날짜의 형식에 맞지 않을 경우 예외처리함.
            floatingToast("유효한 날짜 값이 아닙니다.\n다시 확인해주세요.")

            // false값을 반환해 TodoList에 추가하지 않음
            return false
        }

        // 오늘 날짜 이전의 값이 입력되었다면
        if( inputDate < currentDate ) {
            floatingToast("오늘 이전의 날짜는 입력할 수 없습니다. \n다시 입력해주세요.")

            //false값을 반환하여 리스트에 추가할 수 없도록 함
            return false
        }
        return true

        // 입력한 날짜가 오늘 날짜를 포함한 이후이면
        // else {
            /*
            return when {
                // 각 날짜의 입력값이 null이 아니라면
                (year != null) && (month != null) && (day != null)  -> {
                    // 입력한 월에 대한 최대 일수 확인절차
                    when(month) {
                        1, 3, 5, 7, 8, 10, 12 -> day in 1..31
                        4, 6, 9, 11 -> day in 1..30
                        // 2월은 윤달 확인절차도 거침
                        2 -> {
                            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) day in 1..29
                            else day in 1..28
                        }
                        else -> {
                            // 월에 따른 최대 일수를 넘어가거나, 윤달 관련 잘못된 입력이 있을 경우 띄우는 토스트
                            floatingToast("날짜가 잘못 입력되었으니 다시 확인 해주세요.")
                            false
                        }
                    }
                }
                else -> {
                    // 각 날짜의 입력값이 null이거나 위 범위에 해당하지 않는다면 띄우는 토스트
                    floatingToast("날짜가 잘못 입력되었으니 다시 확인 해주세요.")
                    false
                }
            }
            */
            //return true
        //}
    }

    // 무엇을 할지, 언제 할지에 대한 값이 빈 문자열이거나 null이라면 잘못된 입력에 대한 toast를 띄움
    private fun isValidString(whattodo: String?, whenYear: String?, whenMonth: String?, whenDay: String?, inputCycle: String?): Boolean {
        if ( whattodo.isNullOrBlank() || whenYear.isNullOrBlank() || whenMonth.isNullOrBlank() || whenDay.isNullOrBlank() || inputCycle == "목표 사이클 수를 설정하세요.") {
            floatingToast("모든 칸에 입력해주세요.")
            return false
        }
        return true
    }

    // 토스트띄우기 함수
    private fun floatingToast(message: String) = Toast.makeText(binding?.root?.context, message, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}