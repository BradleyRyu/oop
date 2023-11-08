package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myprojectapplication.databinding.FragmentSetUserBinding


class SetUserFragment : Fragment() {

    var binding: FragmentSetUserBinding? = null

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

        binding?.userImage?.setImageResource(R.drawable.offline)

        binding?.btnUserIdChange?.setOnClickListener {
            // 유저 아이디 변경, 서버 구축
        }

        binding?.btnFindFriendsId?.setOnClickListener {
            // 서버에서 친구 검색, 서버 구축
        }

        /*

        // 유저 아이디 변경 후 버튼을 누르면 서버에 전송
        // 서버 구축 및 데이터 베이스 구축 필요 (MVVM 중 Model 구축 필요)
        binding?.btnUserIdChange?.setOnClickListener {

        }

        */
    }

}