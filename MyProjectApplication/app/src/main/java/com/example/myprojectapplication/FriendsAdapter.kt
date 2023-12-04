package com.example.myprojectapplication

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.FriendsListUnitBinding
import com.example.myprojectapplication.databinding.FriendslistPopupBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

class FriendslistPopupFragment: DialogFragment() {
    var binding: FriendslistPopupBinding? = null
    val viewModel: TodoViewModel by activityViewModels()

    // 팝업창을 띄우기 위한 일종의 루틴
    override fun onStart() {
        super.onStart()

        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
            it.window?.setGravity(Gravity.CENTER)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = FriendslistPopupBinding.inflate(inflater) // 팝업창을 바인딩한다.

        val bundle = this.arguments  // arguments를 통해 번들을 가져옴
        val friendId = bundle?.getString("id")

        // let을 통해 번들을 통해 받아온 친구리스트 중 친구의 아이디를 it으로 사용
        friendId.let {
            binding?.popupTitle?.text = it // 팝업창의 타이틀을 friendId로 설정
            viewModel.currentUserId?.let {
                val id = it
                binding?.btnDelete?.setOnClickListener {
                    Toast.makeText(binding?.root?.context, "${friendId}가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    friendId?.let { viewModel.deleteFriend(id, it) } // 친구 삭제 함수
                    dismiss()
                }

                // 같이하기 버튼을 누른 경우 토스트를 띄운다.
                binding?.btnWithfriend?.setOnClickListener {
                    Toast.makeText(binding?.root?.context, "${friendId}와 함께하기를 시도합니다.", Toast.LENGTH_SHORT).show()
                    dismiss() // 토스트를 띄운 후 팝업창을 닫기 위한 함수
                }
            }
        }

        builder.setView(binding?.root)
        return builder.create()
    }
}

class FriendsAdapter(var friendsList: MutableList<FriendData>): RecyclerView.Adapter<FriendsAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = FriendsListUnitBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(friendsList[position]) // 이 때의 친구리스트는 업데이트 된 친구리스트이다.

    override fun getItemCount(): Int = friendsList.size


    class Holder(private val binding: FriendsListUnitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friendData: FriendData) {
            val friendId = friendData.id
            val friendState = friendData.state

            binding.txtId.text = friendId
            binding.txtState.text = friendState

            binding.root.setOnClickListener {// 토스트 하기 위한 코드
                Toast.makeText(binding.root.context, "ID : $friendId State : $friendState",
                    Toast.LENGTH_SHORT).show()
            }

            binding.btnPopupFriends.setImageResource( when ( friendState ) {
                "ONLINE" -> R.drawable.online
                "OFFLINE" -> R.drawable.offline
                else -> R.drawable.user
            })

            // 이미지를 누를 경우 팝업창을 띄운다.
            binding.btnPopupFriends.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("id", friendId)
                val friendPopupWindow = FriendslistPopupFragment()
                friendPopupWindow.arguments = bundle // 팝업창에 들어갈 내용을 전달하기 위해 번들로 전달
                friendPopupWindow.show((it.context as AppCompatActivity).supportFragmentManager, "FriendsListPopupFragment")
            }
        }
    }
}