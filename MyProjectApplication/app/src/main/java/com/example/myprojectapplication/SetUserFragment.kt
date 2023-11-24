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

        binding?.txtUserId?.text = id
        binding?.userImage?.setImageResource(R.drawable.user)

        binding?.btnFindFriendsId?.setOnClickListener {
            val newFriend = binding?.txtNewfriends?.text.toString() // 사용자가
            val state = id?.let { it1 -> viewModel.observeFriendState(it1, newFriend).toString() }
            viewModel.checkUserExist(newFriend).observe(viewLifecycleOwner) {
                when ( it ) {
                    true -> {
                        state?.let { it1 -> viewModel.addNewFriends(id, newFriend, it1) }
                        Toast.makeText(binding?.root?.context, "$newFriend Append", Toast.LENGTH_SHORT).show()
                    }
                    else -> Toast.makeText(binding?.root?.context, "Not exist Friend!!", Toast.LENGTH_SHORT).show()
                }
            }
            binding?.txtNewfriends?.text = null
        }
    }
}