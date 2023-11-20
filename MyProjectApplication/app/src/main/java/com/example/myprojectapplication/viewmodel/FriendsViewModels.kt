package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myprojectapplication.FriendData
import com.example.myprojectapplication.FriendsList
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.repository.UserRepository

data class UserDataClass(
    val id: String = "",
    val todo: List<TodoList> = emptyList(),
    var time: Int = 0,
    var state: Boolean = false,
    val friendsList: List<FriendsList> = emptyList()
)

class TodoViewModel : ViewModel() {
    private val todoRepository = UserRepository()
    private val _userLiveData = MutableLiveData<UserDataClass>()
    val userLiveData: LiveData<UserDataClass> get() = _userLiveData

    private val _friendsLiveData = MutableLiveData<List<FriendData>>()
    val friendsLiveData: LiveData<List<FriendData>> get() = _friendsLiveData

    fun observeFriendsList(id: String): LiveData<List<FriendData>> {
        todoRepository.observeFriendsList(id, _friendsLiveData)
        return friendsLiveData
    }

    // 사용자 정보 가져오기
    fun observeUser(id: String): LiveData<UserDataClass> {
        todoRepository.observeUser(id, _userLiveData)
        return userLiveData
    }

    // 할일 항목 추가
    fun updateTodoItem(id: String, newItem: MutableList<TodoList>) {
        todoRepository.updateTodoItem(id, newItem)
    }

    // 할일 항목 제거
    fun removeTodoItem(id: String, index: Int) {
        todoRepository.removeTodoItem(id, index)
    }

    fun updateTime(id: String, newTime: Int) {
        todoRepository.updateTime(id, newTime)
    }

    fun addNewFriends(id: String, newFriends: String, state: String) {
        val newFriend = FriendData(newFriends, state)
        todoRepository.addNewFriends(id, newFriend)
    }
}