package com.example.myprojectapplication

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.databinding.FriendsListUnitBinding
import com.example.myprojectapplication.databinding.FriendslistPopupBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

class FriendslistPopupFragment: Fragment() {
    var binding: FriendslistPopupBinding? = null
    val viewModel: TodoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FriendslistPopupBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnDelete?.setOnClickListener {
            println("Helllo")
            Toast.makeText(binding?.root?.context, "Delete!!!",
                Toast.LENGTH_SHORT).show()
        }

        binding?.btnWithfriend?.setOnClickListener {
            Toast.makeText(binding?.root?.context, "With Friend!!!",
                Toast.LENGTH_SHORT).show()
        }
    }
}

class FriendsAdapter(var friendsList: MutableList<FriendData>): RecyclerView.Adapter<FriendsAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = FriendsListUnitBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(friendsList[position])

    override fun getItemCount(): Int = friendsList.size

    class Holder(private val binding: FriendsListUnitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friendData: FriendData) {

            binding.txtId.text = friendData.id
            binding.txtState.text = friendData.state

            when (friendData.state) {
                "OFFLINE" -> R.drawable.offline
                "ONLINE" -> R.drawable.online
            }
            binding.root.setOnClickListener {// 토스트 하기 위한 코드
                Toast.makeText(binding.root.context, "ID : ${friendData.id} State : ${friendData.state}",
                    Toast.LENGTH_SHORT).show()
            }

            binding.btnPopupFriends.setOnClickListener {
                val popupView = LayoutInflater.from(it.context).inflate(R.layout.friendslist_popup, null)
                val mBuilder = AlertDialog.Builder(it.context)
                    .setView(popupView)
                mBuilder.show()
            }

            binding.btnPopupFriends.setImageResource(when(friendData.state) {
                "OFFLINE" -> R.drawable.offline
                else -> R.drawable.online
            })

        }
    }
}