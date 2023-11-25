package com.example.myprojectapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myprojectapplication.viewmodel.UserDataClass
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.database.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TimerViewModel(private val userId: String, private val _todo: TodoList) : ViewModel() {
    private val db by lazy { Firebase.database }

    private val user = db.reference
        .child("users")
        .child(userId)
        .snapshots
        .map { it.getValue(UserDataClass::class.java) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val todo = user.map {
        it?.todo?.firstOrNull { it.thing_Todo == _todo.thing_Todo }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /*
     Achieved Cycle +1 증가
     @return true: 남은 사이클 없음, false: 남은 사이클 있음
     */
    fun addAchievedCycle(): Boolean {
        val user = user.value ?: return true
        val todo = todo.value ?: return true

        val index = user.todo.indexOfFirst { it.thing_Todo == todo.thing_Todo }
        if (index < 0) return true

        val remainCycle = todo.remainCycle

        Log.d("TimerViewModel", "Remain cycle: $remainCycle")

        val reference = db.reference
            .child("users")
            .child(userId)
            .child("todo")
            .child(index.toString())

        Log.d("TimerViewModel", reference.toString())

        reference
            .child("achievedCycle")
            .setValue(ServerValue.increment(1))

        if (remainCycle <= 1) {
            reference.child("checked").setValue(true)
            return true
        }

        return false
    }

    /*
     Temp Cycle +1 증가
     */
    fun addTempCycles() {
        db.reference
            .child("users")
            .child(userId)
            .child("tempCycles")
            .setValue(ServerValue.increment(1))
    }
}

class TimerViewModelFactory(private val userId: String, private val todo: TodoList) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TimerViewModel(userId, todo) as T
    }
}