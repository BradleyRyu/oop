package com.example.myprojectapplication.repository

import androidx.lifecycle.MutableLiveData
import com.example.myprojectapplication.FriendData
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.viewmodel.UserDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class UserRepository {
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users") // 예시로 "users"를 사용

    fun updateUser(id: String, newUser: UserDataClass) {
        userRef.child(id).setValue(newUser)
    }

    // 사용자 정보 가져오기
    fun observeUser(id: String, userLiveData: MutableLiveData<UserDataClass>) {
        userRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserDataClass::class.java)
                userLiveData.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
    }

    fun observeFriendsList(id: String, friendsLiveData: MutableLiveData<List<FriendData>>) {
        userRef.child(id).child("friendsList").addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendsList = snapshot.getValue<List<FriendData>>()
                friendsLiveData.postValue(friendsList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addNewFriends(id: String, newFriends: FriendData) {
        userRef.child(id).child("friendsList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentFriendsList = snapshot.getValue<List<FriendData>>() ?: emptyList()
                val newFriendsList = currentFriendsList.toMutableList() + newFriends
                userRef.child(id).child("friendsList").setValue(newFriendsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // exception
            }
        })
    }

    fun addTodoItem(id: String, newItem: TodoList) {
        userRef.child(id).child("todo").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentTodoList = snapshot.getValue<List<TodoList>>() ?: emptyList()
                val updatedTodoList = currentTodoList.toMutableList()
                updatedTodoList.add(newItem)
                userRef.child(id).child("todo").setValue(updatedTodoList)
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
    }
    fun updateTodoItem(id: String, updatedTodoList: MutableList<TodoList>) {
        userRef.child(id).child("todo").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userRef.child(id).child("todo").setValue(updatedTodoList)
            }
            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
    }

    // 할일 항목 제거
    fun removeTodoItem(id: String, index: Int) {
        userRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(UserDataClass::class.java) ?: return
                if (index >= 0 && index < currentUser.todo.size) {
                    val updatedTodoList = currentUser.todo.toMutableList()
                    updatedTodoList.removeAt(index)
                    val updatedUser = currentUser.copy(todo = updatedTodoList)
                    updateUser(id, updatedUser)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
    }

    fun updateTime(id: String, newTime: Int) {
        userRef.child(id).child("time").setValue(newTime)
    }
}
