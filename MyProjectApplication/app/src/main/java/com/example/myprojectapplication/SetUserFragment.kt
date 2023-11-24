package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myprojectapplication.databinding.FragmentSetUserBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

class SetUserFragment : Fragment() {

    var binding: FragmentSetUserBinding? = null
    val viewModel: TodoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetUserBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: String? = viewModel.currentUserId

        binding?.txtUserId?.text = id
        id.let {
            viewModel.currentUserId?.let {
                binding?.userImage?.setImageResource( when ( viewModel.observeUserState(it) ) {
                    true -> R.drawable.online
                    else -> R.drawable.offline
                })
            }
        }

        binding?.btnFindFriendsId?.setOnClickListener { // 친구 추가 버튼을 누를 때 작동
            viewModel.currentUserId?.let {
                val userId = it
                val newFriend = binding?.txtNewfriends?.text.toString() // 사용자가 입력한 친구 아이디를 받는다.
                val state = viewModel.observeFriendState(it, newFriend).toString() // 친구의 상태 받아오기

                viewModel.checkUserExist(newFriend).observe(viewLifecycleOwner) {   // 새로운 친구가 파이어베이스에 존재하는지 파악
                    when (it) {
                        true -> {
                            viewModel.addNewFriends(userId, newFriend, state)
                        }
                        else -> Toast.makeText(binding?.root?.context, "Not exist Friend!!", Toast.LENGTH_SHORT).show()
                    }
                }
                binding?.txtNewfriends?.text = null
            }
        }
    }
}