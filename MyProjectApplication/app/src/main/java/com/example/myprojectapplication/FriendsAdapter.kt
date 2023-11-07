package com.example.myprojectapplication

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.FragmentFriendsListBinding
import com.example.myprojectapplication.databinding.FriendsListUnitBinding
import com.example.myprojectapplication.viewmodel.FriendsViewModel
class FriendsAdapter(val friends: Array<Friends>): RecyclerView.Adapter<FriendsAdapter.Holder>() {

//    val viewModel: FriendsViewModel by  activityViewModels() // viewmodel init

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
            binding.imageView.setImageResource( when( friend.state ) {
                State.OFFLINE -> R.drawable.offline
                State.ONLINE -> R.drawable.online
            })

            binding.txtId.text = friend.id

            binding.txtState.text = when ( friend.state ) {
                State.OFFLINE -> "OFFLINE"
                State.ONLINE -> "ONLINE"
            }
            // blue color F008EFF
            // 온,오프라인 상태에 따른 글자색 변경
            when (friend.state) {
                State.OFFLINE -> binding.txtState.setTextColor(Color.RED)
                State.ONLINE -> binding.txtState.setTextColor(Color.BLUE)
            }


            // 토스트 하기 위한 코드
            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "ID : ${friend.id} State : ${friend.state}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}