package kr.ac.kpu.ecobasket

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Cabinet(val name : String? = null,           //보관함이름
                   val QRCode : String? = null,         //보관함 고유 QR
                   var remain : Int = 0,               //남은 장바구니 개수
                   var isOpen : Boolean = false         //개폐여부
                   ) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "name" to name,
            "QRCode" to QRCode,
            "remain" to remain,
            "isOpen" to isOpen
        )
    }
}