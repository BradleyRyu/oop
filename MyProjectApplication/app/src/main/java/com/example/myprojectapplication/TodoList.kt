package com.example.myprojectapplication

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.lang.Integer.max

@Parcelize
@IgnoreExtraProperties
data class TodoList(
    var thing_Todo: String? = null,
    var year_Todo: Int? = null,
    var month_Todo: Int? = null,
    var day_Todo: Int? = null,
    var goalCycle: Int? = null,
    var achievedCycle: Int? = null,
    var isChecked: Boolean = false
) : Parcelable {
    @get: Exclude
    @set: Exclude
    @IgnoredOnParcel
    var isSelected: Boolean = false

    @get: Exclude
    @IgnoredOnParcel
    val remainCycle get() = max((goalCycle ?: 0) - (achievedCycle ?: 0), 0)
}

/*
TodoList 수정했습니다!
@Parcelize : 객체를 다른 액티비티로 전달할 때 사용
@IgnoreExtraProperties : Firebase의 특정 필드 무시
@Exclude : Firbase에 저장안할 속성
이렇게 생각하시면 될 것 같아요.
 */