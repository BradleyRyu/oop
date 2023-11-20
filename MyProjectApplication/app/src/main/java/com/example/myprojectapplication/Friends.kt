package com.example.myprojectapplication


data class FriendData(
    var id: String = "",
    var state: String = ""
)

data class FriendsList(var friendsId: String? = null, var friendsState: String? = null)