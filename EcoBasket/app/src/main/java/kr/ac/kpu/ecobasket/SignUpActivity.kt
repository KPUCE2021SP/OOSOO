package kr.ac.kpu.ecobasket

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.toast

class SignUpActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var usersRef = Firebase.database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //FirebaseAuth, DB 인스턴스 가져오기
        auth = FirebaseAuth.getInstance()


        //회원가입 버튼
        join_btn.setOnClickListener {
            var email = edit_email.text.toString().trim()
            var password1 = edit_pw1.text.toString().trim()
            var password2 = edit_pw2.text.toString().trim()
            var nickname = edit_nickname.text.toString().trim()
            var phoneNum = edit_phoneNum.text.toString().trim()
            var pattern = android.util.Patterns.EMAIL_ADDRESS;

            //각각의 예외처리 한 후 새로운 계정 만들기 함수 실행
            if (email == "" || password1 == "" || password2 == ""){
                toast("이메일 또는 패스워드는 필수로 입력해야합니다.")
            } else if (password1.length < 6 || password2.length <6){
                toast("비밀번호는 6자리 이상이어야 합니다. 다시 입력해주십시오.")
            } else if (password1 != password2){
                toast("비밀번호가 다릅니다. 다시 확인해주십시오.")
            } else if(!pattern.matcher(email).matches()){
                toast("이메일 형식을 다시 확인해주십시오.")
            } else {
                createUser(email, password1, nickname, phoneNum)
            }
        }

        //뒤로 돌아가기 버튼
        btn_back2.setOnClickListener{
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

                    updateUI(user)
                    createUserDB(name, phone, email)
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
        }
    }

    //DB에 회원정보 넣기
    private fun createUserDB(name: String?, phone: String?, email: String){
        val user = User(name = name, phone = phone, mileage = 30, isUsing = false, level = 1, email = email, theme = "island")

        usersRef.child(auth.currentUser?.uid.toString()).setValue(user.toMap()).addOnSuccessListener {
            Log.i("firebase", "Successful Create User")
        }.addOnFailureListener{ Log.w("firebase","Failure Create User")}
        //사용중인 장바구니정보 추가해야함
    }
}
