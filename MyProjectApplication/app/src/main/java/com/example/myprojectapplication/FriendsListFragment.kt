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
    private val viewModel: TodoViewModel by activityViewModels()

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
        viewModel.currentUserId?.let { // 사용자가 유효한 경우에만 실행
            viewModel.updateFriendsList(it) // 자신의 친구상태를 업데이트하는 메서드
            viewModel.observeFriendsList(it).observe(viewLifecycleOwner, Observer {friendsList ->
                friendsAdapter.friendsList = friendsList.toMutableList()
                friendsAdapter.notifyDataSetChanged() // 아답터에게 업데이트된 내용을 전달하기 위한 루틴
            })
        }
        binding?.recFriends?.adapter = friendsAdapter // 아답터 루틴
        binding?.recFriends?.layoutManager = LinearLayoutManager(context)
    }
}