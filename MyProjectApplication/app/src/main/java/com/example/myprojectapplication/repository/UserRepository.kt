package com.example.myprojectapplication.repository

import android.util.Log
import android.util.MutableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myprojectapplication.FriendData
import com.example.myprojectapplication.FriendsList
import com.example.myprojectapplication.TodoList
import com.example.myprojectapplication.viewmodel.UserDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
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
                val friendsList = snapshot.children.mapNotNull {
                    it.getValue(FriendData::class.java)
                }
                friendsLiveData.postValue(friendsList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun observeFriendState(id: String, friendId: String, stateLiveData: MutableLiveData<String>) {
        userRef.child(friendId).child("state").addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = snapshot.getValue(Boolean::class.java)?:false
                stateLiveData.postValue(state.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun addNewFriends(id: String, newFriendId: String) {
        userRef.child(newFriendId).child("state").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendState = snapshot.getValue(Boolean::class.java) ?: false
//                val stateString = if (friendState) "ONLINE" else "OFFLINE"
                val stateString = when(friendState) {
                    true -> "ONLINE"
                    else -> "OFFLINE"
                }
                val newFriend = FriendData(newFriendId, stateString)

                userRef.child(id).child("friendsList").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val friendsList = snapshot.children.mapNotNull {
                            it.getValue(FriendData::class.java)
                        }
                        if (!friendsList.any { it.id == newFriendId }) {
                            val newFriendsList = friendsList.toMutableList() + newFriend
                            userRef.child(id).child("friendsList").setValue(newFriendsList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // error handling
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // error handling
            }
        })
    }


//    fun addTodoItem(id: String, newItem: TodoList) {
//        userRef.child(id).child("todo").addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val currentTodoList = snapshot.getValue<List<TodoList>>() ?: emptyList()
//                val updatedTodoList = currentTodoList.toMutableList()
//                updatedTodoList.add(newItem)
//                userRef.child(id).child("todo").setValue(updatedTodoList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // 에러 처리
//            }
//        })
//    }
    fun updateTodoItem(id: String, updatedTodoList: MutableList<TodoList>) {
        userRef.child(id).child("todo").setValue(updatedTodoList)
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


    fun deleteFriend(id: String, deleteId: String) {
        userRef.child(id).child("friendsList").addListenerForSingleValueEvent( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 친구가 없는 경우 발생하는 오류를 방지하기 위해 emptylist를 반환할 수 있도록 한다.
                val currentFriendsList = snapshot.children.mapNotNull {
                    it.getValue(FriendData::class.java)
                }
                val newFriendsList = currentFriendsList.filter { it.id != deleteId }.toMutableList()
                userRef.child(id).child("friendsList").setValue(newFriendsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // exception
            }
        })
    }

    fun updateTimeTodo(id: String, thing_Todo: String, newTimeTodo: Int) {
        val todoListRef = userRef.child(id).child("todo")
        todoListRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val todoItem = childSnapshot.getValue(TodoList::class.java)
                    Log.d("Firebase", "Retrieved todoItem: $todoItem")
                    if (todoItem?.thing_Todo == thing_Todo) {
                        val todoItemRef = todoListRef.child(childSnapshot.key!!)
                        val updateMap = mapOf<String, Any>("achievedCycle" to newTimeTodo)
                        todoItemRef.updateChildren(updateMap)
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase", "아 외않돼냐고")
            }
        })
    }

    fun getTimeTodo(id: String, thing_Todo: String): LiveData<Int?> {
        val timeTodoLiveData = MutableLiveData<Int?>()
        val todoListRef = userRef.child(id).child("todo")
        todoListRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val todoItem = childSnapshot.getValue(TodoList::class.java)
                    if (todoItem?.thing_Todo == thing_Todo) {
                        timeTodoLiveData.postValue(todoItem?.achievedCycle)
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase", "아 외않돼냐고")
            }
        })
        return timeTodoLiveData
    }


    fun updateTempCycles(id: String, studyTime: Int) {
        userRef.child(id).child("tempCycles").setValue(studyTime)
    }

    fun updateStudyCycles(id: String, studyCycles: List<Int>) {
        userRef.child(id).child("studyCycles").setValue(studyCycles)
    }


    fun getTempCycles(id: String): LiveData<Int> {
        val tempCyclesLiveData = MutableLiveData<Int>()
        userRef.child(id ).child("tempCycles").get().addOnSuccessListener {
            tempCyclesLiveData.value = it.getValue(Int::class.java) ?: 0
        }
        return tempCyclesLiveData
    }

    fun checkUserExist(id: String): LiveData<Boolean> {
        val exist = MutableLiveData<Boolean>()
        userRef.child(id).addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exist.postValue(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
        return exist
    }


    fun changeUserState(id: String, state: Boolean) {
        userRef.child(id).child("state").setValue(state)
    }

    fun updateDate(id: String, date: String) {
        userRef.child(id).child("date").setValue(date)
    }


    /*
    fun observeTempCycles(id: String): LiveData<Int> {
        val tempCycles = MutableLiveData<Int>()
        userRef.child(id).child("tempCycles").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tempCycles.value = dataSnapshot.value.toString().toInt()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error here
            }
        })
        return tempCycles
    }
     */
    fun observeTempCycles(userId: String): LiveData<Int> {
        val tempCyclesLiveData = MutableLiveData<Int>()
        database.getReference("users/$userId/tempCycles").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Long::class.java)?.let { value ->
                    val tempCycles = value.toInt()  // Long 타입의 값을 Int로 변환
                    tempCyclesLiveData.postValue(tempCycles)
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        return tempCyclesLiveData
    }


    fun getUserState(id: String): MutableLiveData<Boolean> {
        val state = MutableLiveData<Boolean>()
        userRef.child(id).addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                state.value = snapshot.value as Boolean
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
        return state
    }
}