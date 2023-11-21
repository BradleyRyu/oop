package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myprojectapplication.databinding.FragmentSetUserBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

class SetUserFragment : Fragment() {

    var binding: FragmentSetUserBinding? = null
    val viewModel: TodoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetUserBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding?.btnBackfriendslist?.setOnClickListener {
//            findNavController().navigate(R.id.action_setUserFragment_to_friendsListFragment2)
//        }

        // 사용자의 상태에 따른 이미지 변경
        // binding?.userImage?.setImageResource()

        binding?.btnFindFriendsId?.setOnClickListener {
            binding?.txtNewfriends?.text?.toString().let {
                viewModel.addNewFriends("asdf", binding?.txtNewfriends?.text.toString(), "ONLINE")
                Toast.makeText(binding?.root?.context, "Append Friend!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}