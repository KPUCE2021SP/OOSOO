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
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Retrofit

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
            finish()
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

    private fun doLogin(userEmail: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    startActivity<MainActivity>()
                    //메인 액티비티
                    toast("로그인 성공!")
                } else{
                    toast("로그인 정보가 일치하지 않습니다.")
                }
            }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Google Login", "signInWithCredential:success")
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Google Login", "signInWithCredential:failure", it.exception)
                }
            }
    }

    private val kakaoAPIServe by lazy {
        KakaoRequest.create()
    }

    var disposable: Disposable? = null

    private fun kakaoLogin() {
        // 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoLogin", "로그인 실패", error)
            }
            else if (token != null) {
                Log.i("KakaoLogin", "로그인 성공 ${token.accessToken}")

                disposable = kakaoAPIServe.getFirebaseToken(token.accessToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            Log.d("KakaoLogin", result)

                            val json = JSONObject(result)
                            val res = json.getString("firebase_token")
                            Log.d("KakaoLogin", res)

                            Firebase.auth.signInWithCustomToken(res)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("KakaoLogin", "signInWithCustomToken:success")
                                        finish()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("KakaoLogin", "signInWithCustomToken:failure", task.exception)
                                        toast("Authentication failed.")
                                    }
                                }
                        },
                        { error -> Log.d("KakaoLogin", "error:" + error.message.toString())}
                    )
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

}