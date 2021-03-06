package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_preferences.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class PreferencesActivity : AppCompatActivity() {
    lateinit var memberAdapter : PrefRecyAdapter
    lateinit var supportAdapter : PrefRecyAdapter
    lateinit var infoAdapter : PrefRecyAdapter
    val list_member = mutableListOf<String>()
    val list_support = mutableListOf<String>()
    val list_info = mutableListOf<String>()

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        auth = FirebaseAuth.getInstance()

        val usersRef = Firebase.database.getReference("users").child("${auth.currentUser?.uid}")

        setSupportActionBar(toolbar_pref)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecycler()

        memberAdapter.setItemClickListener(object: PrefRecyAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //클릭 이벤트
                when (position) {
                    0 -> { // 회원정보 전체 수정
                        // 회원정보수정 액티비티
                        startActivity<ModifyActivity>()
                        finish()
                    }

                    1 -> { //로그아웃하고 로그인화면으로 돌아가기
                        alert(title = "로그아웃", message = "로그아웃 하시겠습니까?") {
                            positiveButton("확인") {
                                Firebase.auth.signOut()
                                UserApiClient.instance.logout { error ->
                                    if (error != null) {
                                        Log.e("KakaoLogout", "로그아웃 실패. Invalid Token")
                                    }
                                    else {
                                        Log.i("KakaoLogout", "로그아웃 성공")
                                    }
                                }
                                toast("로그아웃 되었습니다.")
                                finish()
                                startActivity<LoginActivity>()
                            }
                            negativeButton("취소") {
                            }
                        }.show()
                    }

                    2 -> { //회원탈퇴하고 로그인화면으로 돌아가기
                        alert(title = "회원 탈퇴", message = "회원 탈퇴 하시겠습니까?") {
                            positiveButton("확인") {
                                //DB삭제 후 auth삭제
                                usersRef.removeValue()
                                    .addOnSuccessListener{
                                        auth.currentUser?.delete()
                                        UserApiClient.instance.unlink { error ->
                                            if (error != null) {
                                                Log.e("KakaoUnlink", "연결 끊기 실패", error)
                                            }
                                            else {
                                                Log.i("KakaoUnlink", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                                            }
                                        }
                                        toast("${auth.currentUser?.email} 회원탈퇴 되었습니다.")
                                        finish()
                                        startActivity<LoginActivity>()
                                    }
                            }
                            negativeButton("취소") {
                            }
                        }.show()
                    }
                }
            }
        })

        supportAdapter.setItemClickListener(object: PrefRecyAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //클릭 이벤트
                when (position) {
                    0 -> {
                        startActivity<RequestActivity>()
                    }

                    1 -> {
                        startActivity<ReportActivity>()
                    }
                }
            }
        })

        infoAdapter.setItemClickListener(object: PrefRecyAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //클릭 이벤트
                when (position) {
                    1 -> {
                        alert(title = "Team OOSOO", message = "팀장 박찬호(2017152017)\n" +
                                "팀원 김재현(2017150009)\n" +
                                "팀원 김진호(2017150011)") {
                            positiveButton("확인") {

                            }
                        }.show()
                    }
                }
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initRecycler() {
        memberAdapter = PrefRecyAdapter(this)
        supportAdapter = PrefRecyAdapter(this)
        infoAdapter = PrefRecyAdapter(this)

        recy_member.adapter = memberAdapter
        recy_support.adapter = supportAdapter
        recy_info.adapter = infoAdapter

        list_member.apply {
            add("회원정보 수정")
            add("로그아웃")
            add("회원 탈퇴")
        }
        list_support.apply {
            add("요청하기")
            add("신고하기")
        }
        list_info.apply {
            add("프로그램 버전 1.0")
            add("개발자 정보")
        }

        memberAdapter.datas = list_member
        memberAdapter.notifyDataSetChanged()

        supportAdapter.datas = list_support
        supportAdapter.notifyDataSetChanged()

        infoAdapter.datas = list_info
        infoAdapter.notifyDataSetChanged()
    }
}