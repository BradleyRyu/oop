package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.FragmentTodoBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

class TodoFragment : Fragment() {

    private var binding: FragmentTodoBinding? = null

    /*
    private var param1: String? = null
    private var param2: String? = null

     */

    private var todoList: MutableList<TodoList> = mutableListOf()
    val viewModel: TodoViewModel by activityViewModels()
    var id: String = ""

    // ItemTouchHelper를 선언 ( recycler view에 삭제를 위한 swipe를 지원하는 유틸리티 클래스. swipe가 일어나기 전까지는 불릴 일이 없으므로, by lazy로 선언함.)
    private val itemTouchHelper by lazy {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback( // up과 down쪽 스와이프는 사용하지 않을 것이므로, 파라미터는 0
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove( // 현재 어플에서는 사용하지 않는 함수임
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // 스와이프 시 해당 아이템 삭제
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 현재 viewholder의 어댑터 위치를 가져옴
                val position = viewHolder.adapterPosition
                //만약 recTodo의 어댑터가 TodoAdapter의 인스턴스(메모리에 올라간 객체)라면, TodoAdapter의 removeList를 사용해 해당 위치의 아이템을 제거함.
                (binding?.recTodo?.adapter as? TodoAdapter)?.removeList(position)
                viewModel.removeTodoItem(id, position)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false)
        //layoutmanager: 리사이클러뷰에 어떻게 아이템을 쌓을 것인가, 어떤 형식으로 보여줄 것이냐
        binding?.recTodo?.layoutManager = LinearLayoutManager(context)

        // ItemTouchHelper를 RecyclerView에 연결
        itemTouchHelper.attachToRecyclerView(binding?.recTodo)

        // 할 일을 추가하는 버튼을 클릭하면 생기는 이벤트.
        binding?.btnAdd?.setOnClickListener {
            // 추가할 todolist에 대한 정보를 받고 기존 리스트에 추가함
            // AddToList fragment로부터 읽어온 값을 추가함 (할 일과 날짜를 받아옴) -> 추후 구현
            // 상단에 있는 텍스트 입력창을 통해 할 일과 언제 할 일인지를 읽고 값을 추가함
            // input year, month, day에 입력한 값을 정수로 변환하여 함수를 통해 입력 받음

            val whatToDo: String = binding?.inputWhattodo?.text.toString()
            val inputYear: String = binding?.inputYear?.text.toString()
            val inputMonth: String = binding?.inputMonth?.text.toString()
            val inputDay: String = binding?.inputDay?.text.toString()

            // 입력받은 문자열의 유효성을 검사하는 과정. Integer.parseInt() 함수에서 널값이나 빈 칸인 값이 오면 프로그램이 오류로 종료됨
            if( isValidString(whatToDo, inputYear, inputMonth, inputDay) ) {
                addList( whatToDo,
                    inputYear.toInt(),
                    inputMonth.toInt(),
                    inputDay.toInt(),
                    false
                )
            }
            // 추가하기 버튼을 누를 시, 칸을 비움
            makeTextBoxesBlank()
        }

        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = arguments?.getString("id").toString()

        // ViewModel에서 사용자의 투두리스트 데이터를 관찰합니다.
        viewModel.observeUser(id).observe(viewLifecycleOwner) { userData ->
            // userData가 null이 아니면 투두리스트를 띄우기
            userData?.let {
                // 투두리스트를 UI에 띄우는 코드
                // 예: todoListAdapter.submitList(it.todo)
                todoList = it.todo.toMutableList()
                binding?.recTodo?.adapter = TodoAdapter(todoList)
            }
        }

        // 기존 코드 생략...
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
    private fun sortTodoList(todoList: MutableList<TodoList>): MutableList<TodoList> {
        return todoList.sortedWith(
            compareBy(
                { it.year_Todo },
                { it.month_Todo },
                { it.day_Todo }
            )
        ).toMutableList()
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
            todoList.add(TodoList(whattodo, whentodo_year, whentodo_month, whentodo_day, donetodo))
            // 어댑터에게 아이템이 삽입되었다는 것을 알려줌 (notifyDataSetChanged를 사용하려 했으나 안드로이드 공식 문서에서 특정 작업에 대한 notify를 쓸 것을 권장하였음.
            todoList = sortTodoList(todoList)  // 초기 어댑터를 만들기 전에 목록을 정렬합니다.
            // 입력된 리스트를 포함하여 다시 todo리스트 정렬하고
            binding?.recTodo?.adapter?.notifyItemInserted(todoList.size)
//            viewModel.addTodoItem("users", TodoList(whattodo, whentodo_year, whentodo_month, whentodo_day, donetodo))
            // 정렬된 리스트로 어댑터 갱신
            binding?.recTodo?.adapter = TodoAdapter(todoList)
            viewModel.addTodoItem(id, TodoList(whattodo, whentodo_year, whentodo_month, whentodo_day, donetodo))
            true
        } else {
            false
        }
    }
    //사용자 편의를 위해 할 일에 적은 것은 남겨둠
    private fun makeTextBoxesBlank() {
        binding?.inputWhattodo?.setText("")
        binding?.inputYear?.setText("")
        binding?.inputMonth?.setText("")
        binding?.inputDay?.setText("")
    }

    // 입력한 날짜 값이 유효한 값인지 검사함. ( 각 월의 최대 일수 및 년도에 따른 윤달 체크 )
    private fun isValidDate(year: Int?, month: Int?, day: Int?): Boolean {
        return when {
            // 각 날짜의 입력값이 null이 본 어플의 제적 연도인 2023년보다 작지 않으면서 입력한 월이 1월에서 12월 범위 안에 들어있다면
            (year != null) && (month != null) && (day != null) && (year >= 2023) && (month in 1..12) -> {
                when(month) {
                    1, 3, 5, 7, 8, 10, 12 -> day in 1..31
                    4, 6, 9, 11 -> day in 1..30
                    2 -> {
                        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) day in 1..29
                        else day in 1..28
                    }
                    else -> { // 윤달 관련 잘못된 입력이 있을 경우 띄우는 토스트
                        Toast.makeText(binding?.root?.context, "날짜가 잘못 입력되었으니 확인 바랍니다.", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
            }
            else -> { // 각 날짜의 입력값이 null이거나 위 범위에 해당하지 않는다면 띄우는 토스트
                Toast.makeText(binding?.root?.context, "날짜가 잘못 입력되었으니 확인 바랍니다.", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    // 무엇을 할지, 언제 할지에 대한 값이 빈 문자열이거나 null이라면 잘못된 입력에 대한 toast를 띄움
    private fun isValidString(whattodo: String?, whenYear: String?, whenMonth: String?, whenDay: String?): Boolean {
        if ( whattodo.isNullOrBlank() || whenYear.isNullOrBlank() || whenMonth.isNullOrBlank() || whenDay.isNullOrBlank() ) {
            Toast.makeText(binding?.root?.context, "모든 칸에 입력 바랍니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}