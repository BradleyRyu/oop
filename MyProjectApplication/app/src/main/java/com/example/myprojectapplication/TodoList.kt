package com.example.myprojectapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoList(var thing_Todo: String? = null,
                    var year_Todo: Int? = null,
                    var month_Todo: Int? = null,
                    var day_Todo: Int? = null,
                    var time_Todo: Int? = null,
                    var isChecked: Boolean = false,
                    var done_Todo: Boolean = false):Parcelable