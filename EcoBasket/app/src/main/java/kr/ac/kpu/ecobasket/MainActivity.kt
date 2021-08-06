package kr.ac.kpu.ecobasket

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View.*
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var isRentMode = true

    @RequiresApi(Build.VERSION_CODES.M) //getColor 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toast("${Firebase.auth.currentUser}")

        // 로그인 안되어있을 시에 로그인 화면
        if (Firebase.auth.currentUser == null){
            startActivity(
                Intent(this, LoginActivity::class.java))
        }

        btn_menu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.RIGHT)
            boxInfoCard.visibility = GONE   //보관함 정보 숨기기
        }

        //메인 - 섬꾸미기 아이콘
        btn_island.setOnClickListener {
            startActivity<IslandActivity>()
            boxInfoCard.visibility = GONE   //보관함 정보 숨기기
        }

        //메인 - 대여버튼 & 반납 (상태변화로 구현)
        btn_rent.setOnClickListener {
            if(isRentMode) {
                /* QR코드리더기 액티비티 구현 */
                startActivityForResult<QRActivity>(0)
            }
            else {
                btn_rent.text = "대여하기"
                btn_rent.setBackgroundColor(getColor(R.color.btn_rent_color))
                isRentMode = !isRentMode
            }

            /*
            startActivityForResult<QRActivity>(requestCode)
               //+Database연동
             */

        }

        //** 임시(MapView에서 보관함 id 불러오는 것으로 수정) */
        btn_testBoxInfo.setOnClickListener {
            boxInfoCard.visibility = VISIBLE
        }

        map.setOnClickListener{
            boxInfoCard.visibility = GONE
        }

        //메인 - 우측메뉴바
        nav_menu.setNavigationItemSelectedListener(this)

        nav_menu.getHeaderView(0).setOnClickListener {
            startActivity<MyInfoActivity>()
        }
    }

    //메뉴바 메뉴 연결
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_guide ->
                startActivity<GuideActivity>()
            R.id.menu_pref ->
                startActivity<PreferencesActivity>()
        }
        return false
    }

    //메뉴바 닫기 액션 구현
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    //QRActivity 리턴값 받는 용도
    @RequiresApi(Build.VERSION_CODES.M) //getColor 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    //QR찍었을 때
                    btn_rent.text = "반납하기"
                    btn_rent.setBackgroundColor(getColor(R.color.btn_return_color))
                    isRentMode = !isRentMode
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            when (requestCode) {
                0 -> {
                    //QR안찍고 돌아왔을때
                }
            }
        }
    }
}