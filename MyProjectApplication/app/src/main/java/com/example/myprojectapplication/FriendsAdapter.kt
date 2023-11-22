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

    // 팝업창 사이즈 조절
    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setGravity(Gravity.CENTER)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = FriendslistPopupBinding.inflate(inflater)

        val bundle = this.arguments  // arguments를 통해 번들을 가져옴
        val friendId = bundle?.getString("id")?:"null"

        binding?.popupTitle?.text = friendId
        val id = viewModel.currentUserId?:"null"

        binding?.btnDelete?.setOnClickListener {
            Toast.makeText(binding?.root?.context, "$friendId  Delete Friend...", Toast.LENGTH_SHORT).show()
            viewModel.deleteFriend(id, friendId) // 친구 삭제 함수
            dismiss()
        }

        binding?.btnWithfriend?.setOnClickListener {
            Toast.makeText(binding?.root?.context, "$friendId  With Friend!!!",
                Toast.LENGTH_SHORT).show()
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

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(friendsList[position])

    override fun getItemCount(): Int = friendsList.size


    class Holder(private val binding: FriendsListUnitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friendData: FriendData) {

            binding.txtId.text = friendData.id
            binding.txtState.text = friendData.state

            binding.root.setOnClickListener {// 토스트 하기 위한 코드
                Toast.makeText(binding.root.context, "ID : ${friendData.id} State : ${friendData.state}",
                    Toast.LENGTH_SHORT).show()
            }

            binding.btnPopupFriends.setImageResource(when(friendData.state) {
                "OFFLINE" -> R.drawable.offline
                else -> R.drawable.online
            })

            binding.btnPopupFriends.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("id", friendData.id)
                bundle.putString("state", friendData.state)
                val friendPopupWindow = FriendslistPopupFragment()
                friendPopupWindow.arguments = bundle
                friendPopupWindow.show((it.context as AppCompatActivity).supportFragmentManager, "FriendslistPopupFragment")
            }
        }
    }
}