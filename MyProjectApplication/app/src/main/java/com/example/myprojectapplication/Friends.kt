package com.example.myprojectapplication

import androidx.lifecycle.ViewModel
import com.example.myprojectapplication.viewmodel.FriendsViewModels

enum class State {
    OFFLINE,
    ONLINE
}

data class Friends(val id: String, val state: State) {}