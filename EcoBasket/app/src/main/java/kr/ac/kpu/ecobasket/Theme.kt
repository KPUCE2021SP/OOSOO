package kr.ac.kpu.ecobasket

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Theme(var default : Boolean = true,
                 var spring : Boolean = true,
                 var summer : Boolean = false,
                 var autumn : Boolean = false,
                 var winter : Boolean = false,
                 var sunset : Boolean = false,
                 var usingLevel : Int = 1
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "default" to default,
            "spring" to spring,
            "summer" to summer,
            "autumn" to autumn,
            "winter" to winter,
            "sunset" to sunset,
            "usingLevel" to usingLevel
        )
    }
}
