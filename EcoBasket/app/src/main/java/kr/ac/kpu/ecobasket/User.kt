package kr.ac.kpu.ecobasket

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val UID : String, // UserID(Identifier)
                val name : String? = null,   //유저 이름
                val phone : String? = null,   //유저 전화번호
                var mileage : Int? = 0,     //마일리지
                var isUsing : Boolean? = false,
                var useInfo : Map<String, String>? = null,   //사용 정보(QR, 사용시작시간)
                var email : String? = null
                ) {
    //유저 클래스 구조화
}