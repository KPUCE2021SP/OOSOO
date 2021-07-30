package kr.ac.kpu.ecobasket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.toast

class SignUpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //뒤로 돌아가기 버튼
        back_btn2.setOnClickListener{
            finish()
        }

        //DB에 회원가입 정보 넣는 함수

        //*********예외처리 조건문 많이 필요*********

        join_btn.setOnClickListener {
            /*
            edit_email.text.toString()
            edit_pw1.text.toString()
            edit_pw2.text.toString()
            edit_nickname.text.toString()
            edit_phoneNum.text.toString()
            */

            //예외처리 예시
            if(edit_pw1.text.toString() != edit_pw2.text.toString()){
                toast("비밀번호가 다릅니다. 다시 확인해주십시오.")
            }else{
                //정보 DB에 넣고 finish()
            }

            if(edit_pw1.text.length < 6 || edit_pw1.text.length < 6){
                toast("비밀번호는 6자리 이상이어야 합니다. 다시 입력해주십시오.")
            }else{
                //정보 DB에 넣고 finish()
            }
        }
    }
}
