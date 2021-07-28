package kr.ac.kpu.ecobasket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Login_btn.setOnClickListener {
            val userEmail = EMail_edit.text.toString()
            val password = PW_edit.text.toString()
            doLogin(userEmail, password)
        }
    }

    private fun doLogin(userEmail: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    startActivity(Intent(this, MainActivity::class.java))
                    //메인 액티비티
                    toast("이메일: $userEmail 비밀번호: $password 로 로그인 성공!")
                    finish()
                } else{
                    toast("로그인 실패")
                }
            }
        }

}