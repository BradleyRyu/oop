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
    // 서버를 이용해서 친구들의 상태를 확인할 수 있어야 한다. -> 서버에 데이터를 어떻게 저장해야할까?

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
            viewModel.observeFriendsList(it).observe(viewLifecycleOwner, Observer {friendsList ->
                friendsAdapter.friendsList = friendsList.toMutableList()
                friendsAdapter.notifyDataSetChanged()
            })
        }
        binding?.recFriends?.adapter = friendsAdapter
        binding?.recFriends?.layoutManager = LinearLayoutManager(context)
    }
}