package kr.ac.kpu.ecobasket

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class History(val date : String? = null,           //대여한 날짜
                   val location : String? = null,         //대여 위치
                   var status : Boolean = false         //반납 여부
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "date" to date,
            "location" to location,
            "status" to status
        )
    }
}