package kr.ac.kpu.ecobasket

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

//유저 클래스 구조화
@IgnoreExtraProperties
data class User(val name : String? = null,   //유저 이름
                val phone : String? = null,   //유저 전화번호
                var level : Int? = 1,        //유저 레벨
                var mileage : Int? = 0,     //마일리지
                var isUsing : Boolean? = false,
                var email : String? = null
                ) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "name" to name,
            "phone" to phone,
            "level" to level,
            "mileage" to mileage,
            "isUsing" to isUsing,
            "email" to email
        )
    }

}