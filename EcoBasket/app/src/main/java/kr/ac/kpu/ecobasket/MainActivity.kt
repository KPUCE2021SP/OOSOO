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
import com.google.firebase.auth.FirebaseAuth
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

    var database = Firebase.database.reference
    private val cabinetRef = Firebase.database.getReference("Cabinet")
    private val auth = FirebaseAuth.getInstance()
    private val usersRef = Firebase.database.getReference("users").child("${auth.currentUser?.uid}")
    var user : User? = null //현재 로그인한 user 정보 객체

    @RequiresApi(Build.VERSION_CODES.M) //getColor 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 로그인 안되어있을 시에 로그인 화면
        if (Firebase.auth.currentUser == null){
            startActivity(
                Intent(this, LoginActivity::class.java))
        }else{
            queryUserInformation()  //user 객체 초기화
        }

        //우측 메뉴바
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
            if(user?.isUsing!!) {   //대여
                /* QR코드리더기 액티비티 구현 */
                startActivityForResult<QRActivity>(1000)
            }
            else {  //반납
                startActivityForResult<QRActivity>(2000)
            }
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

    //유저 정보 읽기 쿼리문
    private fun queryUserInformation() {
        usersRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue<User>()    //user 객체화
                //테스트 코드 (성공 확인)
                Log.i("firebase", "Got value $user")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException())
            }
        })
    }

    //위치 데이터 읽기(Map API 후 수정)
    private fun queryCabinetLocation(locX : Int, locY : Int) {
        cabinetRef.child("${locX}_${locY}").addListenerForSingleValueEvent(  object : ValueEventListener {
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


    //QRActivity 리턴값 받는 용도(대여, 반납 : requestCode로 구분)
    @RequiresApi(Build.VERSION_CODES.M) //getColor 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //1000번 : 대여하기 QR
        if(requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) { //QR찍었을 때
                btn_rent.text = "반납하기"
                btn_rent.setBackgroundColor(getColor(R.color.btn_return_color))
                usersRef.child("isUsing").setValue(true)
            } else if(resultCode == Activity.RESULT_CANCELED) {
                //QR안찍고 돌아왔을때
                toast("대여 QR 촬영 Canceled")
            }

        }
        //2000번 : 반납하기 QR
        else if(requestCode == 2000) {
            if(resultCode == Activity.RESULT_OK) {
                btn_rent.text = "대여하기"
                btn_rent.setBackgroundColor(getColor(R.color.btn_rent_color))
                usersRef.child("isUsing").setValue(false)
            }
            else if(resultCode == Activity.RESULT_CANCELED) {
                toast("반납 QR 촬영 Canceled")
            }

        }
    }





}