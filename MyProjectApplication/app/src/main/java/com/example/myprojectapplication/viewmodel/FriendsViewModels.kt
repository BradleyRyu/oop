package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendsViewModels: ViewModel() {
    private val _id = MutableLiveData<String>("Goosmos")
    val id: LiveData<String> get() = _id

    private val _state = MutableLiveData<String>("OFFLINE")
    val state: LiveData<String> get() = _state
}