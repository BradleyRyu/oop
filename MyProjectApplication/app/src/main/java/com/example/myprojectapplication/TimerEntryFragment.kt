package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.myprojectapplication.databinding.FragmentTimerEntryBinding

//리사이클러 뷰 크기 반으로 줄이고 그래프 추가

class TimerEntryFragment : Fragment() {

    var binding:FragmentTimerEntryBinding? = null

    //바인딩 처리
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimerEntryBinding.inflate(inflater)

        // Inflate the layout for this fragment
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnMoveCalender?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_calenderFragment)
        }

        binding?.btnMoveFriends?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_friendsListFragment)
        }

        binding?.btnMoveTimer?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_timerFragment)
        }

        binding?.btnMoveTodo?.setOnClickListener {
            findNavController().navigate(R.id.action_timerEntryFragment_to_addToListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}