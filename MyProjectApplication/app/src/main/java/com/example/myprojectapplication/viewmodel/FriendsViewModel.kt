package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendsViewModel: ViewModel() {

//    private val repository = FriendsRepository()
//    init {
//        repository.obverseState(_state)
//    }

    // 내부적으로는 바꿀 수 있는 데이터, 외부에서는 변경 불가
    private var _id = MutableLiveData<String>("Goosmos")
    val id: LiveData<String> get() = _id
}
