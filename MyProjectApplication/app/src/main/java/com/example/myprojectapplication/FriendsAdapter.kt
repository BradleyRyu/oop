package com.example.myprojectapplication

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.FriendsListUnitBinding
import com.example.myprojectapplication.databinding.FriendslistPopupBinding
import com.example.myprojectapplication.viewmodel.FriendsViewModels

//class FriendslistPopupFragment: Fragment() {
//    var binding: FriendslistPopupBinding? = null
//    val viewModel: FriendsViewModels by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FriendslistPopupBinding.inflate(inflater)
//        return binding?.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel.id.observe(viewLifecycleOwner) {
//            binding?.popupTitle?.text = viewModel.id.value
//        }
//
//        binding?.btnDelete?.setOnClickListener {
//            Toast.makeText(binding?.root?.context, "Delete!!!",
//                Toast.LENGTH_SHORT).show()
//        }
//
//        binding?.btnWithfriend?.setOnClickListener {
//            Toast.makeText(binding?.root?.context, "With Friend!!!",
//                Toast.LENGTH_SHORT).show()
//        }
//    }
//}

class FriendsAdapter(val friends: Array<Friends>): RecyclerView.Adapter<FriendsAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = FriendsListUnitBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun getItemCount() = friends.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(friends[position])
    }

    class Holder(private val binding: FriendsListUnitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friends) {
            binding.txtId.text = friend.id

            binding.txtState.text = when ( friend.state ) {
                State.OFFLINE -> "OFFLINE"
                State.ONLINE -> "ONLINE"
            }

            binding.btnPopupFriends.setImageResource( when ( friend.state ) {
                State.OFFLINE -> R.drawable.offline
                State.ONLINE -> R.drawable.online
            })

            when (friend.state) { // 온,오프라인 상태에 따른 글자색 변경
                State.OFFLINE -> binding.txtState.setTextColor(Color.RED)
                State.ONLINE -> binding.txtState.setTextColor(Color.BLUE)
            }

            binding.root.setOnClickListener {// 토스트 하기 위한 코드
                Toast.makeText(binding.root.context, "ID : ${friend.id} State : ${friend.state}",
                    Toast.LENGTH_SHORT).show()
            }

//            binding.btnPopupFriends.setOnClickListener {
//
//            }

            binding.btnPopupFriends.setOnClickListener {
                val popupView = LayoutInflater.from(it.context).inflate(R.layout.friendslist_popup, null)
                val mBuilder = AlertDialog.Builder(it.context)
                    .setView(popupView)
                mBuilder.show()
            }

        }
    }
}