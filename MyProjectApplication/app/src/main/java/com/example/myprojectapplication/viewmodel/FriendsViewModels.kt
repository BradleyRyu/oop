package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myprojectapplication.FriendData
import com.example.myprojectapplication.FriendsList
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.repository.UserRepository

data class UserDataClass(
    //val id: String = "",
    var todo: MutableList<TodoList> = mutableListOf(),
    var time: Int = 0,
    var state: Boolean = false,
    val friendsList: List<FriendsList> = emptyList(),

    //각요일별 학습량 배열 추가
    val studyCycles: List<Int> ? = null, // 요일별 학습량
    var date :String = ""
)

class TodoViewModel : ViewModel() {
    private val todoRepository = UserRepository()
    private val _userLiveData = MutableLiveData<UserDataClass>()
    val userLiveData: LiveData<UserDataClass> get() = _userLiveData

    private val _friendsLiveData = MutableLiveData<List<FriendData>>()
    val friendsLiveData: LiveData<List<FriendData>> get() = _friendsLiveData

    private val _friendStateLiveData = MutableLiveData<String>()
    val friendStateLiveData: LiveData<String> get() = _friendStateLiveData


    var currentUserId: String? = null
    var tempCycle: Int = 0

    fun observeFriendsList(id: String): LiveData<List<FriendData>> {
        // 조건
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


    fun addNewFriends(id: String, newFriends: String, state: String) {
        val newFriend = FriendData(newFriends, state)
        todoRepository.addNewFriends(id, newFriend)
    }

    fun deleteFriend(id: String, deleteId: String) {
        todoRepository.deleteFriend(id, deleteId)
    }

    fun updateTimeTodo(id: String, thing_Todo: String, newTime: Int) {
        todoRepository.updateTimeTodo(id, thing_Todo, newTime)
    }

    fun getTimeTodo(id: String, thing_Todo: String): LiveData<Int?> {
        return todoRepository.getTimeTodo(id, thing_Todo)
    }

    fun updateTempCycles(id: String, studyTime: Int) {
        todoRepository.updateTempCycles(id, studyTime)
    }

    fun updateStudyCycles(id: String, studyCycles: MutableList<Int>) {
        todoRepository.updateStudyCycles(id, studyCycles)
    }

    //템프사이클 관찰
    fun observeTempForUpdate(id: String): LiveData<UserDataClass> {
        todoRepository.observeUser(id, _userLiveData)
        todoRepository.getTempCycles(id).observeForever { tempCycle ->
            this.tempCycle = tempCycle
        }
        return userLiveData
    }

    fun checkUserExist(id: String): LiveData<Boolean> {
        return todoRepository.checkUserExist(id)
    }

    fun observeFriendState(id: String, friendId: String): LiveData<String> {
        todoRepository.observeFriendState(id, friendId, _friendStateLiveData)
        return friendStateLiveData
    }

    fun observeUserState(id: String):Boolean {
        return true
    }

    fun updateDate(id: String, date: String) {
        todoRepository.updateDate(id, date)
    }

    fun observeTempCycles(id: String): LiveData<Int> {
        return todoRepository.observeTempCycles(id)
    }






}