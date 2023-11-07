package com.example.myprojectapplication

data class TodoList(var thing_Todo: String?,
                    var year_Todo: Int?,
                    var month_Todo: Int?,
                    var day_Todo: Int?,
                    // var time_Todo: Int?,
                    var done_Todo: Boolean = false)