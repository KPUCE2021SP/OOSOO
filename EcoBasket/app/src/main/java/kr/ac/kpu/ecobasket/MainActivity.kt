package kr.ac.kpu.ecobasket

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.View.*
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val database = Firebase.database
    val cabinetRef = database.getReference("Cabinet")
    //val userRef = database.getReference("Users")

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
        val testLocationClickListener = View.OnClickListener { view ->
            boxInfoCard.visibility = VISIBLE

            when(view.id) {
                R.id.testLocation1 -> queryCabinetLocation(123, 123)
                R.id.testLocation2 -> queryCabinetLocation(234, 234)
                R.id.testLocation3 -> queryCabinetLocation(777, 777)
            }
        }
        testLocation1.setOnClickListener(testLocationClickListener)
        testLocation2.setOnClickListener(testLocationClickListener)
        testLocation3.setOnClickListener(testLocationClickListener)

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

    //위치 데이터 읽기(Map API 후 수정)
    private fun queryCabinetLocation(locX : Int, locY : Int) {
        cabinetRef.child("${locX}_${locY}").addListenerForSingleValueEvent(
            object : ValueEventListener {      //남은 과제 : 데이터베이스 구조 깔끔하게, 쿼리문 숙지할 것
                override fun onDataChange(snapshot: DataSnapshot) {
                    /* Map 강제 형변환 식
                     * var cabinet = snapshot.value as Map<*, *>   //객체화
                     * textBoxTitle.text = cabinet["name"].toString()
                     * textCabinetQR.text = cabinet["QRCode"].toString()
                     * textRemainBacket.text = "잔여 바구니 : ${cabinet["remain"].toString()}"
                     * */

                    var cabinet = snapshot.getValue<Cabinet>()   //객체화
                    //map.keys.toString()
                    textCabinetTitle.text = cabinet?.name.toString()
                    textCabinetQR.text = cabinet?.QRCode.toString()
                    textRemainBacket.text = "잔여 바구니 : ${cabinet?.remain.toString()}"

                    //테스트 코드 (성공 확인)
                    Log.i("firebase", "Got value ${cabinet}")
                }

                override fun onCancelled(error: DatabaseError) {
                    //읽음 요청 실패시
                    Log.e("firebase", "Error getting data")
                }

        })
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