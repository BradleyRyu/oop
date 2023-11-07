package com.example.myprojectapplication

enum class State {
    OFFLINE,
    ONLINE
}

data class Friends(val id: String, val state: State)