package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding?.btnBackfriendslist?.setOnClickListener {
            findNavController().navigate(R.id.action_setUserFragment_to_friendsListFragment2)
        }
        binding?.txtNewfriends?.text.toString().let {
            binding?.btnFindFriendsId?.setOnClickListener {
                viewModel.addNewFriends("asdf", binding?.txtNewfriends?.text.toString(), "OFFLINE")
            }
        }


        binding?.btnFindFriendsId?.setOnClickListener {
            val newFriendsId = binding?.txtNewfriends?.text.toString()
            viewModel.addNewFriends("asdf", newFriendsId, "OFFLINE")
        }

    }
}