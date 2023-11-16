package com.example.myprojectapplication.repository

import androidx.lifecycle.MutableLiveData
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.viewmodel.UserDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository {
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users") // 예시로 "users"를 사용

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

    // 사용자 정보 업데이트
    fun updateUser(id: String, newUser: UserDataClass) {
        userRef.child(id).setValue(newUser)
    }

    // 할일 항목 추가
    fun addTodoItem(id: String, newItem: TodoList) {
        userRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(UserDataClass::class.java) ?: return
                val updatedTodoList = currentUser.todo.toMutableList()
                updatedTodoList.add(newItem)
                val updatedUser = currentUser.copy(todo = updatedTodoList)
                updateUser(id, updatedUser)
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
        userRef.setValue(id, newItem)
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
}
