package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_layout.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* 로그인정보 GET
        val userEmail = intent.getStringExtra("userEmail")
        val password = intent.getStringExtra("password")
        */

        btn_menu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.RIGHT)
        }

        //메인 - 섬꾸미기 아이콘
        btn_island.setOnClickListener {
            startActivity<IslandActivity>()
        }

        //메인 - 대여버튼
        btn_rent.setOnClickListener {
            btn_return.visibility = VISIBLE     //** 버튼 일치화 필요 (xml 상태지정) */
            btn_rent.visibility = GONE

            /* QR코드리더기 액티비티 구현 */
            startActivity<QRActivity>()
            /*
            startActivityForResult<QRActivity>(requestCode)
               //+Database연동
             */

        }

        //** 임시(상태지정) */
        btn_return.setOnClickListener {
            btn_rent.visibility = VISIBLE
            btn_return.visibility = GONE
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
}