package kr.ac.kpu.ecobasket

import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface KakaoRequest {
    @GET("/verifyToken/{token}")
    fun getFirebaseToken(@Path("token") token: String): Observable<String>

    companion object {
        var gson = GsonBuilder()
            .setLenient()
            .create()

        fun create(): KakaoRequest {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://192.168.55.13:8000")
                .build()

            return retrofit.create(KakaoRequest::class.java)
        }
    }
}