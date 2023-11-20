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

class FriendsAdapter(val friends: List<Friends>?): RecyclerView.Adapter<FriendsAdapter.Holder>() {
    //파라미터를 수정해서 파이어베이스 데이터를 읽어올 수 있도록 할 것

    class ViewHolder(val binding: FriendsListUnitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friends?) {
            friend?.let {
                binding.txtId.text = it.id.toString()
                binding.txtState.text = it.state.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = FriendsListUnitBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = friends?.size?: 0

    class Holder(private val binding: FriendsListUnitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friends) {
            class holderViewModel: ViewModel() {
                private val repository = FriendRepository()
                private val _id = MutableLiveData<String>()
                val id: LiveData<String> get() = _id


                private val _state = MutableLiveData<String>()
                val state: LiveData<String> get() = _state

                init {
                    repository.observeFriends(_id)
                }

            }

            binding.txtId.text = friend.id.toString()

            binding.txtState.text = friend.state.toString()

//            binding.btnPopupFriends.setImageResource( when ( friend.state.toString() ) {
//                "OFFLINE" -> R.drawable.offline
//                "ONLINE" -> R.drawable.online
//            })

            when (friend.state.toString()) { // 온,오프라인 상태에 따른 글자색 변경
                "OFFLINE" -> binding.txtState.setTextColor(Color.RED)
                "ONLINE" -> binding.txtState.setTextColor(Color.BLUE)
            }

            binding.root.setOnClickListener {// 토스트 하기 위한 코드
                Toast.makeText(binding.root.context, "ID : ${friend.id} State : ${friend.state}",
                    Toast.LENGTH_SHORT).show()
            }

            binding.btnPopupFriends.setOnClickListener {
                val popupView = LayoutInflater.from(it.context).inflate(R.layout.friendslist_popup, null)
                val mBuilder = AlertDialog.Builder(it.context)
                    .setView(popupView)

                mBuilder.show()
            }

        }
    }
}