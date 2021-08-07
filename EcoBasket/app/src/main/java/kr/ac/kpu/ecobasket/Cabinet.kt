package kr.ac.kpu.ecobasket

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Cabinet(val name : String? = null,           //보관함이름
                   val location : String? = null,       //보관함 위치(X_Y의 형태)
                   val QRCode : String? = null,         //보관함 고유 QR
                   var remain : Int? = 0,               //남은 장바구니 개수
                   var isOpen : Boolean = false         //개폐여부
                   ) {
    override fun toString(): String {
        return super.toString()
    }
}