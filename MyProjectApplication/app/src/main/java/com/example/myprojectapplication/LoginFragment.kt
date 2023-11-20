package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myprojectapplication.databinding.FragmentFriendsListBinding
import com.example.myprojectapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    var binding: FragmentLoginBinding? = null
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = binding?.inputId?.text?.let {

        }.toString()

        binding?.btnLogin?.setOnClickListener {
            val inputId = binding?.inputId?.text.let {
                binding?.inputId?.text
            }.toString()

            val bundle = Bundle()
            id.let {
                bundle.putString("id", inputId)
            }

            val todoFragment = TodoFragment()
            val timerEntry = TimerEntryFragment()
//            val friendsList = Fr

            findNavController().navigate(R.id.action_loginFragment_to_calenderFragment)
            // 번들로 전달하기
        }


    }
}