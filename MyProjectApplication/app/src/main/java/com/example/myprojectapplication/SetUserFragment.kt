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

    private fun check(userId: String, newFriend: String) {
        // 파라미터로 전달받은 유저 친구이름을 탐색한다. 만약 친구이름이 존재하는 경우 친구를 추가한다.
        // 만약 존재하지 않는 친구인 경우 존재하지 않는다는 문구를 Toast한다.
        viewModel.checkUserExist(newFriend).observe(viewLifecycleOwner) { exist ->
            when ( exist ) {
                true -> {
                    viewModel.addNewFriends(userId, newFriend)
                    Toast.makeText(binding?.root?.context, "${newFriend}가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(binding?.root?.context, "${newFriend}는 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.txtNewfriends?.text = null // 입력을 받은 이후에 텍스트 창을 비워준다.
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentUserId?.let { // 현재 접속한 아이디가 null이 아닌 경우에만 실행
            val userId = it
            viewModel.observeUser(userId).observe(viewLifecycleOwner) {user ->
                user?.let {
                    binding?.switchState?.isChecked = user.state // 널이 아닌 경우 유저의 상태를 바로 반영한다.
                }?: run {
                    binding?.switchState?.isChecked = false
                // 가장 처음 파이어베이스에 상태가 입력되지 않았을 때, 즉 null인 상황에서
                // 스위치의 default를 false로 설정하기 위해 run 사용
                }
            }

            // 사용자가 스위치를 누르면 파이어베이스의 상태를 변경
            binding?.switchState?.setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeUserState(userId, isChecked)
            }

            binding?.btnFindFriendsId?.setOnClickListener {
                when ( val newFriend = binding?.txtNewfriends?.text.toString() ) {
                    // 조건문을 통해 입력받은 아이디가 자신과 같은 경우에 Toast생성
                    userId -> {
                        Toast.makeText(binding?.root?.context, "자신의 이름은 추가할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    else -> check(userId, newFriend)
                }
            }
        }
    }
}