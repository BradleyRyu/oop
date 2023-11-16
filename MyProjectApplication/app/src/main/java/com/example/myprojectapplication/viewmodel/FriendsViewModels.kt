package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.repository.UserRepository
data class UserDataClass(val id: String, val todo: List<TodoList>, var time: Int = 0 , var state: Boolean = false)

class TodoViewModel : ViewModel() {
    private val todoRepository = UserRepository()
    private val userLiveData = MutableLiveData<UserDataClass>()

    // 사용자 정보 가져오기
    fun observeUser(id: String): LiveData<UserDataClass> {
        todoRepository.observeUser(id, userLiveData)
        return userLiveData
    }

    // 할일 항목 추가
    fun addTodoItem(id: String, newItem: TodoList) {
        todoRepository.addTodoItem(id, newItem)
    }

    // 할일 항목 제거
    fun removeTodoItem(id: String, index: Int) {
        todoRepository.removeTodoItem(id, index)
    }
}

//
//class FriendsViewModels: ViewModel() {
//    private val repository = FriendRepository()
////    private val _id = MutableLiveData<String>("Goosmos")
////    val id: LiveData<String> get() = _id
//
//    private val _user = MutableLiveData<UserDataClass>()
//    val user: LiveData<UserDataClass> get() = _user
//
//    private val userRepository = UserRepository()
//
////    init {
////        repository.observeFriends(_id)
////    }
//
//    private val _state = MutableLiveData<String>("OFFLINE")
//    val state: LiveData<String> get() = _state
//}