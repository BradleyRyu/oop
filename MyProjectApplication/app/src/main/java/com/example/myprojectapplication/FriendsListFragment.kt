package com.example.myprojectapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentFriendsListBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel


class FriendsListFragment : Fragment() {
    var binding: FragmentFriendsListBinding? = null
    val viewModel: TodoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val friendsAdapter = FriendsAdapter(mutableListOf())
        viewModel.currentUserId?.let {
            viewModel.updateFriendsList(it)
            viewModel.observeFriendsList(it).observe(viewLifecycleOwner, Observer {friendsList ->
                friendsAdapter.friendsList = friendsList.toMutableList()
                friendsAdapter.notifyDataSetChanged()
            })
        }
        binding?.recFriends?.adapter = friendsAdapter
        binding?.recFriends?.layoutManager = LinearLayoutManager(context)
    }
}