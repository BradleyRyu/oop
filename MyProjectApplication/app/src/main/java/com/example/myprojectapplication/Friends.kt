package com.example.myprojectapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


const val DEFAULTSTATE = "OFFLINE"
const val DEFAULTID = ""
enum class State {
    OFFLINE,
    ONLINE
}

data class Friends(val temp: Int) {
    private val repository = FriendRepository()
    private val _id = MutableLiveData<String>(DEFAULTID)
    val id: LiveData<String> get() = _id

    private val _state = MutableLiveData<String>(DEFAULTSTATE)
    val state: LiveData<String> get() = _state




}