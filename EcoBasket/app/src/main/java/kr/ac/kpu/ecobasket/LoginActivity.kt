package kr.ac.kpu.ecobasket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var usersRef = Firebase.database.getReference("users")
    private val themeRef = Firebase.database.getReference("Theme")
    var disposable: Disposable? = null
    var createDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Firebase.auth.currentUser != null){
            startActivity<MainActivity>()
            finish()
        }

        Log.d("KakaoKey", Utility.getKeyHash(this))

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

        //회원가입 버튼 누를 시 회원가입 액티비티로 전환
        btn_signUp.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        google_btn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 900)
        }


        kakao_btn.setOnClickListener {
            kakaoLogin()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 900) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Google Login", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google Login", "Google sign in failed", e)
            }
        }
    }

    override fun onDestroy() {
        disposable?.let{ disposable!!.dispose() }
        createDisposable?.let{ createDisposable!!.dispose() }
        super.onDestroy()
    }

    private fun doLogin(userEmail: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    startActivity<MainActivity>()
                    finish()
                    //메인 액티비티
                    toast("로그인 성공!")
                } else{
                    toast("로그인 정보가 일치하지 않습니다.")
                }
            }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Google Login", "signInWithCredential:success")

                    val uid = Firebase.auth.currentUser?.uid!!
                    val email = Firebase.auth.currentUser?.email!!
                    val name = Firebase.auth.currentUser?.displayName
                    val phone = Firebase.auth.currentUser?.phoneNumber
                    Log.d("Database",
                        "\n email : $email" +
                                "\n name : $name" +
                                "\n phone : $phone")

                    createDisposable = createUserDB(uid, name, phone, email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {  //onComplete
                                startActivity<MainActivity>()
                                Log.d("Job", "start Activity")
                                finish()
                                Log.d("Job", "Activity finish")
                                loadingDialog.dismiss()
                            },
                            { error ->  //onError
                                Log.d("Job", error.toString())
                            })

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Google Login", "signInWithCredential:failure", it.exception)
                }
            }
    }

    private val kakaoAPIServe by lazy {
        KakaoRequest.create()
    }

    private fun kakaoLogin() {
        // 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoLogin", "로그인 실패", error)
            }
            else if (token != null) {
                Log.i("KakaoLogin", "로그인 성공 ${token.accessToken}")

                val loadingDialog = LoadingDialog(this)
                loadingDialog.show()

                disposable = kakaoAPIServe.getFirebaseToken(token.accessToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->  // onNext
                            Log.d("KakaoLogin", result)

                            val json = JSONObject(result)
                            val res = json.getString("firebase_token")
                            Log.d("KakaoLogin", res)

                            Firebase.auth.signInWithCustomToken(res)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("KakaoLogin", "signInWithCustomToken:success")

                                        UserApiClient.instance.me { user, error ->
                                            if (error != null) {
                                                Log.e("KakaoUser", "사용자 정보 요청 실패", error)
                                            }
                                            else if (user != null) {
                                                Log.d("KakaoUserIn", "\n name : ${user.kakaoAccount?.profile?.nickname}" +
                                                        "\n email : ${user.kakaoAccount?.email}")

                                                createDisposable = createUserDB(Firebase.auth.currentUser?.uid!!,
                                                    user.kakaoAccount?.profile?.nickname.toString(),
                                                    "", user.kakaoAccount?.email.toString())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(
                                                        {  //onComplete
                                                            Log.d("KakaoLogin", "DB에 추가 성공")
                                                            startActivity<MainActivity>()
                                                            finish()
                                                            loadingDialog.dismiss()
                                                        },
                                                        { error -> Log.d("Job", error.toString()) })  //onError
                                            }
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("KakaoLogin", "signInWithCustomToken:failure", task.exception)
                                        loadingDialog.dismiss()
                                    }
                                }
                        },
                        { error -> Log.d("KakaoLogin", "error:" + error.message.toString()) })  //onError
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    //DB에 회원정보 넣기
    private fun createUserDB(uid: String, name: String?, phone: String?, email: String) : Completable {

        return Completable.create { emitter ->
            usersRef.addValueEventListener(object: ValueEventListener {  // 회원정보가 등록된 UID인지 검사
                override fun onDataChange(snapshot: DataSnapshot) {
                    var result = false

                    val child = snapshot.value as Map<*, *>
                    for (i in child.keys) {
                        Log.d("Database", "key : $i")
                        if(i == uid) {
                            result = true
                            break
                        }
                    }
                    if (result) {  // 이미 등록되어 있을 경우 새로 등록하지 않음
                        Log.d("Database", "Exist User")
                    } else {  // 등록된 회원정보가 없을경우 새로 생성
                        val user = User(name = name, phone = phone, mileage = 0, isUsing = false, level = 1, email = email, theme = "island")

                        usersRef.child(Firebase.auth.currentUser?.uid.toString()).setValue(user.toMap()).addOnSuccessListener {
                            Log.i("Database", "Successful Create User")
                        }.addOnFailureListener{ Log.w("Database","Failure Create User")}

                        val theme = Theme()
                        themeRef.child(Firebase.auth.currentUser?.uid.toString()).setValue(theme.toMap()).addOnSuccessListener {
                            Log.i("firebase", "Successful Create ThemeInfo")
                        }.addOnFailureListener{ Log.w("firebase","Failure Create ThemeInfo")}
                    }

                    emitter.onComplete()
                    Log.d("Job", "onComplete")
                }
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }
            })
        }
    }

}