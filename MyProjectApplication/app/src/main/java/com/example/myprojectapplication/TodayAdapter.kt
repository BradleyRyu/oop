import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.databinding.ListTodayBinding

class TodayAdapter(val todayList: List<TodoList>) :
    RecyclerView.Adapter<TodayAdapter.Holder>() {

    inner class Holder(val binding: ListTodayBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.radioTodo.setOnClickListener {
                val previousCheckedPosition = todayList.indexOfFirst { it.isSelected }
                if (previousCheckedPosition != -1 && previousCheckedPosition != adapterPosition) {
                    // 이전에 체크된 항목 체크 해제
                    todayList[previousCheckedPosition].isSelected = false
                }
                // 현재 아이템 체크 상태 토글
                todayList[adapterPosition].isSelected = !todayList[adapterPosition].isSelected
                notifyDataSetChanged()
            }
        }

        fun bind(today: TodoList) {
            binding.txtToday.text = today.thing_Todo
            binding.radioTodo.isChecked = today.isSelected
            val remainingCycles = (today.goalCycle ?: 0) - (today.achievedCycle ?: 0)
            binding.txtTime.text = remainingCycles.toString()

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListTodayBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun getItemCount() = todayList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(todayList[position])
    }
}