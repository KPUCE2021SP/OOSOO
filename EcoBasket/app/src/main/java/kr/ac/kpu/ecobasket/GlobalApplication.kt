package kr.ac.kpu.ecobasket

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "0091a5e6843ee089e0bcdfe685c63edb")

        // Naver Map SDK 초기화
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient("oqa2gj5y8f")
    }
}