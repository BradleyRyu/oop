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

        val id = viewModel.currentUserId

        binding?.btnFindFriendsId?.setOnClickListener {
            viewModel.currentUserId?.let {
                val userId = it
                val newFriend = binding?.txtNewfriends?.text.toString()

                when ( newFriend ) {
                    userId -> {
                        Toast.makeText(binding?.root?.context, "자신의 이름은 추가할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    else -> {
                        viewModel.observeFriendState(userId, newFriend).observe(viewLifecycleOwner) { state ->
                            viewModel.checkUserExist(newFriend).observe(viewLifecycleOwner) { exist ->
                                when ( exist ) {
                                    true -> {
                                        viewModel.addNewFriends(userId, newFriend)
                                        Toast.makeText(binding?.root?.context, "$newFriend Append", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> Toast.makeText(binding?.root?.context, "$newFriend Not Exist", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        binding?.txtNewfriends?.text = null
                    }
                }
            }
        }

        id?.let {
            binding?.switchState?.setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeUserState(id, isChecked)
            }
        }
    }
}