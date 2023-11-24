package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        (activity as MainActivity).binding?.bottomNav?.visibility = View.GONE

        binding?.btnLogin?.setOnClickListener {
            val inputId = binding?.inputId?.text.toString()
            if(!inputId.isNullOrBlank()){
                // 로그인 성공 후 currentUserId 설정
                todoViewModel.currentUserId = inputId
                // 캘린더뷰로 전환
                findNavController().navigate(R.id.action_loginFragment_to_calenderFragment)
            }
            else {
                Toast.makeText(binding?.root?.context, "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                binding?.inputId?.setText("")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 네비게이션 바 보이기
        (activity as MainActivity).binding?.bottomNav?.visibility = View.VISIBLE
        binding = null
    }

}