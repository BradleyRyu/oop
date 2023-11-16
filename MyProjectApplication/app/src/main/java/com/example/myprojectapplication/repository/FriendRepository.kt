package com.example.myprojectapplication.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FriendRepository {
    val database = Firebase.database
    val idRef = database.getReference("id")

    fun observeFriends(id: MutableLiveData<String>) {
        idRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                id.postValue( snapshot.value.toString() )
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun postId(newValue: String) {
        idRef.setValue(newValue)
    }


}