package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myprojectapplication.FriendsData
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.repository.UserRepository

data class UserDataClass(
    val id: String = "",
    val todo: List<TodoList> = emptyList(),
    var time: Int = 0,
    var state: Boolean = false,
    val friendsList: List<FriendsData> = emptyList()
)

class TodoViewModel : ViewModel() {
    private val todoRepository = UserRepository()
    private val _userLiveData = MutableLiveData<UserDataClass>()
    val userLiveData: LiveData<UserDataClass> get() = _userLiveData

    // 사용자 정보 가져오기
    fun observeUser(id: String): LiveData<UserDataClass> {
        todoRepository.observeUser(id, _userLiveData)
        return userLiveData
    }

    // 할일 항목 추가
    fun addTodoItem(id: String, newItem: TodoList) {
        todoRepository.addTodoItem(id, newItem)
        todoRepository.post(id, newItem)
    }

//    fun updateUser(id: String, newUser: UserDataClass) {
//        userRef.child(id).setValue(newUser)
//    }

    // 할일 항목 제거
    fun removeTodoItem(id: String, index: Int) {
        todoRepository.removeTodoItem(id, index)
    }
}