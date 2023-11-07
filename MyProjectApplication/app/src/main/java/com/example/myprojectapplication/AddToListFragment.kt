package com.example.myprojectapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myprojectapplication.databinding.FragmentAddToListBinding
import java.util.Date

class AddToListFragment : Fragment() {

    private lateinit var binding: FragmentAddToListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = FragmentAddToListBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 각 버튼에 대한 행위 설정
        binding?.btnOk?.setOnClickListener {
            Toast.makeText(binding.root.context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addToListFragment_to_todoFragment)
        }
        binding?.btnCancel?.setOnClickListener {
            Toast.makeText(binding.root.context, "취소되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addToListFragment_to_todoFragment)
        }
    }
    //class의 이름으로 액세스 가능
    /*
    companion object {
        @JvmStatic
        fun newInstance(whattodo: String?, whentodo: Date) = //factory pattern 팩토리 객체의 함수를 통해 간접적으로 객체를 생성하는 역할.
            //bundle은 키값의 쌍을 담는 바구니. 키로 인지되는 값의 쌍 집단을 담는 바구니.
            AddToListFragment().apply {
                arguments = Bundle().apply {
                    putString("text", )
                }
            }
    }
     */

}