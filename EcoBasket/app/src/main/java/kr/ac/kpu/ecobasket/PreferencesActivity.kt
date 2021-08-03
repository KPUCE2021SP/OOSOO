package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_preferences.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
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

        setSupportActionBar(toolbar_pref)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecycler()

        memberAdapter.setItemClickListener(object: PrefRecyAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //클릭 이벤트
                when (position) {
                    0 -> { // 회원정보 전체 수정
                        toast("0번 클릭")
                        // 회원정보수정 액티비티 소환!
                    }

                    1 -> { //로그아웃하고 로그인화면으로 돌아가기
                        toast("1번 클릭")
                        alert(title = "로그아웃", message = "로그아웃 하시겠습니까?") {
                            positiveButton("확인") {
                                Firebase.auth.signOut()
                                toast("로그아웃 되었습니다.")
                                startActivity<LoginActivity>()
                                finish()
                            }
                            negativeButton("취소") {
                            }
                        }.show()
                    }

                    2 -> { //회원탈퇴하고 로그인화면으로 돌아가기
                        toast("2번 클릭")
                        alert(title = "회원 탈퇴", message = "회원 탈퇴 하시겠습니까?") {
                            positiveButton("확인") {
                                auth.currentUser?.delete()
                                toast("${auth.currentUser?.email} 회원탈퇴 되었습니다.")
                                startActivity<LoginActivity>()
                                finish()
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
                        toast("0번 클릭")
                        alert(title = "요청하기", message = "요청하기 다이얼로그") {
                            positiveButton("확인") {

                            }
                            negativeButton("취소") {

                            }
                        }.show()
                    }

                    1 -> {
                        toast("1번 클릭")
                        alert(title = "신고하기", message = "신고하기 다이얼로그") {
                            positiveButton("확인") {

                            }
                            negativeButton("취소") {

                            }
                        }.show()
                    }
                }
            }
        })

        infoAdapter.setItemClickListener(object: PrefRecyAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                //클릭 이벤트
                when (position) {
                    0 -> {
                        toast("0번 클릭")
                    }

                    1 -> {
                        toast("1번 클릭")
                        alert(title = "개발자 정보", message = "개발자 정보 다이얼로그") {
                            positiveButton("확인") {

                            }
                            negativeButton("취소") {

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