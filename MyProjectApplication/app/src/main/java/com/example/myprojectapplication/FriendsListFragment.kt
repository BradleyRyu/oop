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

class FriendsListFragment : Fragment() {

    var binding: FragmentFriendsListBinding? = null

    val friends = arrayOf(
        Friends("Goosmos", State.OFFLINE),
        Friends("Hoo", State.ONLINE),
        Friends("Koo", State.OFFLINE)
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

    }

}