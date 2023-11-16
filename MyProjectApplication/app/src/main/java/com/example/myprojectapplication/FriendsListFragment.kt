package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentFriendsListBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

class FriendsListFragment : Fragment() {
    // 온라인인 친구를 누르면 진행중인 타이머의 상태를 확인하고 같이하기 보낼 수 있도록 수정 필요
    // 서버를 이용해서 친구들의 상태를 확인할 수 있어야 한다. -> 서버에 데이터를 어떻게 저장해야할까?

    var binding: FragmentFriendsListBinding? = null

    val viewModel: TodoViewModel by activityViewModels()

    val friends = arrayOf(
//        Friends("Goosmos", State.OFFLINE),
//        Friends("Hoo", State.ONLINE),
        Friends(0)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsListBinding.inflate(inflater)
        binding?.recFriends?.layoutManager = LinearLayoutManager(this.context)
        binding?.recFriends?.adapter = FriendsAdapter(friends)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnCalender?.setOnClickListener {
            findNavController().navigate(R.id.action_friendsListFragment_to_calenderFragment)
        }
        binding?.btnTodo?.setOnClickListener {
            findNavController().navigate(R.id.action_friendsListFragment_to_todoFragment)
        }
        binding?.btnSetting?.setOnClickListener {
            findNavController().navigate(R.id.action_friendsListFragment_to_setUserFragment2)
        }
    }

}