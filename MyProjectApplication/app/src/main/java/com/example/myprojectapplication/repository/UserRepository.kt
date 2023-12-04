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
                user?.state = snapshot.child("state").getValue(Boolean::class.java) ?: false
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
                friendsLiveData.postValue(friendsList) // 친구리스트를 얻어온다.
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addNewFriends(id: String, newFriendId: String) {
        userRef.child(newFriendId).child("state").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                val friendState = snapshot.getValue(Boolean::class.java) ?: false
                val stateString = when( snapshot.getValue(Boolean::class.java) ?: false ) {
                    true -> "ONLINE"
                    else -> "OFFLINE"
                }

                val newFriend = FriendData(newFriendId, stateString)

                userRef.child(id).child("friendsList").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val friendsList = snapshot.children.mapNotNull {
                            it.getValue(FriendData::class.java)
                        }
                        // 어떠한 친구와도 곂치지 않을 때 친구를 추가
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

    fun updateTempCycles(id: String, studyTime: Int) {
        userRef.child(id).child("tempCycles").setValue(studyTime)
    }

    fun updateStudyCycles(id: String, studyCycles: List<Int>) {
        userRef.child(id).child("studyCycles").setValue(studyCycles)
    }

    fun checkUserExist(id: String): LiveData<Boolean> {
        val exist = MutableLiveData<Boolean>() // 친구 상태가 존재하는지 확인하기 위해
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
        userRef.child(id).child("state").setValue(state) // 스위치에 의한 사용자의 상태 변화 설정
    }

    fun updateDate(id: String, date: String) {
        userRef.child(id).child("date").setValue(date)
    }

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


    fun updateFriendsList(id: String) {
        userRef.child(id).child("friendsList").addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendsList = snapshot.children.mapNotNull {
                    it.getValue(FriendData::class.java)
                }.toMutableList() // 친구의 리스트는 업데이트로 인하여 변경될 수 있다.

                // 친구리스트의 각각에 대한 람다함수를 사용
                friendsList.forEach { friend ->
                    userRef.child(friend.id).child("state").addValueEventListener( object: ValueEventListener {
                        // 루틴을 통해 친구의 상태에 따른 업데이트를 진행한다.
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val friendState = snapshot.getValue(Boolean::class.java) ?: false
                            friend.state = if (friendState) "ONLINE" else "OFFLINE"
                            userRef.child(id).child("friendsList").setValue(friendsList)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            // 에러 처리
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}