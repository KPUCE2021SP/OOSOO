package kr.ac.kpu.ecobasket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.membership_information.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class ModifyActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.membership_information)

        auth = FirebaseAuth.getInstance()

        //기존에 입력한 정보들 불러오기
        mod_email.setText("${auth.currentUser?.email}")

        /* DB에서 가져오기
        mod_nickname.setText("")
        mod_phoneNum.setText("")
         */

        btn_modify.setOnClickListener {
            var email = mod_email.text.toString().trim()
            var password1 = mod_pw1.text.toString().trim()
            var password2 = mod_pw2.text.toString().trim()
            var nickname = mod_nickname.text.toString().trim()
            var phoneNum = mod_phoneNum.text.toString().trim()

            if (email == "" || password1 == "" || password2 == "") {
                toast("이메일 또는 패스워드는 필수로 입력해야합니다.")
            } else if (password1.length < 6 || password2.length < 6) {
                toast("비밀번호는 6자리 이상이어야 합니다. 다시 입력해주십시오.")
            } else if (password1 != password2) {
                toast("비밀번호가 다릅니다. 다시 확인해주십시오.")
            } else {
                updateAuth(email, password1)
                //updateDB(email,password2,nickname,phoneNum)
            }
        }
    }

    //Auth 회원정보변경 함수
    private fun updateAuth(newEmail:String, newPassword:String){

        //이메일 변경
        auth.currentUser?.updateEmail(newEmail)
            ?.addOnCompleteListener(this){
                if(it.isSuccessful){
                    toast("$newEmail 로 이메일이 변경되었습니다.")
                }else{
                    toast("이멜@변경실패")
                }
            }

        //비밀번호변경
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener(this){
                if(it.isSuccessful){
                    toast("$newPassword 로 비밀번호가 변경되었습니다.")
                }else{
                    toast("비번 변경실패")
                }
            }

        //정보 수정이 완료되었다는 대화상자
        alert(title = "회원정보수정", message = "수정이 완료되었습니다.\n3초후에 닫기 버튼을 눌러주십시오."){
            positiveButton("닫기"){
                finish()
            }
        }.show()
    }

    /*DB 회원정보변경 함수
    private fun updateDB(newEmail:String, newPassword:String, newNickname:String, newPhoneNum: String ){

    }*/
}