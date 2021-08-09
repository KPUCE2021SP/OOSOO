package kr.ac.kpu.ecobasket

import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.toast

class SignUpActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //FirebaseAuth, DB 인스턴스 가져오기
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        //회원가입 버튼
        join_btn.setOnClickListener {
            var email = edit_email.text.toString().trim()
            var password1 = edit_pw1.text.toString().trim()
            var password2 = edit_pw2.text.toString().trim()
            var nickname = edit_nickname.text.toString().trim()
            var phoneNum = edit_phoneNum.text.toString().trim()

            //각각의 예외처리 한 후 새로운 계정 만들기 함수 실행
            if (email == "" || password1 == "" || password2 == ""){
                toast("이메일 또는 패스워드는 필수로 입력해야합니다.")
            } else if (password1.length < 6 || password2.length <6){
                toast("비밀번호는 6자리 이상이어야 합니다. 다시 입력해주십시오.")
            } else if (password1 != password2){
                toast("비밀번호가 다릅니다. 다시 확인해주십시오.")
            } else {
                createUser(email, password1, nickname, phoneNum)
            }
        }

        //뒤로 돌아가기 버튼
        back_btn2.setOnClickListener{
            finish()
        }
    }

    //회원가입 함수
    private fun createUser(email: String, password: String, name: String, phone: String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    toast("$email 회원가입 성공!")
                    val user = auth.currentUser
                    val uid = user?.uid.toString()

                    updateUI(user)
                    createUserDB(name, phone, email, uid)
                    finish()
                } else { // 이미 등록된 회원일 경우 fail
                    toast("이미 등록된 회원입니다.")
                    updateUI(null)
                }
            }
            .addOnFailureListener {
                toast("이미 등록된 회원입니다.")
            }
    }

    //회원가입이 정상적으로 되었는지 사용자에게 toast메세지를 띄워주는 함수
    private fun updateUI(user: FirebaseUser?){
        user?.let {
            toast("Email: ${user.email} Uid: ${user.uid}")
        }
    }

    //DB에 회원정보 넣기
    private fun createUserDB(name: String?, phone: String?, email: String, UID: String ){
        val database = Firebase.database
        val usersRef = database.getReference("users")

        val user = User(UID = UID, name = name, phone = phone, mileage = 0, isUsing = false, email = email)

        usersRef.child("$UID").child("name").setValue(name)
        usersRef.child("$UID").child("phone").setValue(phone)
        usersRef.child("$UID").child("email").setValue(email)
        usersRef.child("$UID").child("mileage").setValue(0)
        usersRef.child("$UID").child("isUsing").setValue(false)
        usersRef.child("$UID").child("level").setValue(1)
        //사용중인 장바구니정보 추가해야함

    }
}
