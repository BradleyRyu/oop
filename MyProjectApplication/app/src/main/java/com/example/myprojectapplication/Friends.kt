package com.example.myprojectapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myprojectapplication.repository.UserRepository
import com.example.myprojectapplication.viewmodel.UserDataClass


const val DEFAULTSTATE = "OFFLINE"
const val DEFAULTID = ""
enum class State {
    OFFLINE,
    ONLINE
}

class FriendsData() {

}
/*
class FriendsListViewModel : ViewModel() {
    private val todoRepository = UserRepository()
    private val _userLiveData = MutableLiveData<UserDataClass>()
    val userLiveData: LiveData<UserDataClass> get() = _userLiveData

    init {
        todoRepository.observeUser(_userLiveData)
    }


    fun getFriendsList() = _userLiveData.value?.friendsList
}
*/