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
import com.example.myprojectapplication.databinding.FragmentLoginBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

class LoginFragment : Fragment() {

    var binding: FragmentLoginBinding? = null
    private var userId: String? = null
    private val todoViewModel: TodoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding?.root
    }


    //뷰모델 사용 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnLogin?.setOnClickListener {
            val inputId = binding?.inputId?.text.toString()

            // 로그인 성공 후 currentUserId 설정
            todoViewModel.currentUserId = inputId

            // 캘린더뷰로 전환
            findNavController().navigate(R.id.action_loginFragment_to_calenderFragment)
        }
    }
}