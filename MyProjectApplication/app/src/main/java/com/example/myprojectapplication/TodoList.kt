package com.example.myprojectapplication

data class TodoList(var thing_Todo: String? = null,
                    var year_Todo: Int? = null,
                    var month_Todo: Int? = null,
                    var day_Todo: Int? = null,
                    // var time_Todo: Int?,
                    var done_Todo: Boolean = false)