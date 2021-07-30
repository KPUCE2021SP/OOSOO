package kr.ac.kpu.ecobasket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //로그인 버튼 누를 시 로그인 시도
        btn_login.setOnClickListener {
            val userEmail = EMail_edit.text.toString()
            val password = PW_edit.text.toString()

            if(userEmail == "" || password == ""){
                toast("이메일 또는 비밀번호가 비어있습니다. 다시 입력해주십시오.")
            }else{
                doLogin(userEmail, password)
            }
        }

        btn_test.setOnClickListener {
            startActivity<MainActivity>()
            finish()
        }

        //회원가입 버튼 누를 시 회원가입 액티비티로 전환
        btn_signUp.setOnClickListener {
            startActivity<SignUpActivity>()
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