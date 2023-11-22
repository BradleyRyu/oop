// AlarmComponents.kt

package com.example.myprojectapplication

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myprojectapplication.repository.UserRepository
import com.example.myprojectapplication.viewmodel.TodoViewModel
import com.example.myprojectapplication.viewmodel.UserDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, UpdateService::class.java).apply {
            putExtra("userId", intent.getStringExtra("userId"))
            putExtra("tempCycle", intent.getIntExtra("tempCycle", 0))
        }
        context.startService(serviceIntent)
    }
}


//인텐트에 대해서 자세히 알아보고
//틀린 코드 찾아보기!!!
class UpdateService : IntentService("UpdateService") {
    private val userRepository = UserRepository()

    override fun onHandleIntent(intent: Intent?) {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 (일요일) ~ 6 (토요일)

        // intent에서 사용자의 ID와 tempCycle 값을 가져오기
        val userId = intent?.getStringExtra("userId")
        val tempCycle = intent?.getIntExtra("tempCycle", 0)
        if (userId != null) {
            // tempCycle 값을 사용하여 studyCycles를 업데이트
            userRepository.updateStudyCycles(userId, dayOfWeek.toString(), tempCycle?: 0)
            userRepository.updateTempCycles(userId, 0)
        }
    }
}
