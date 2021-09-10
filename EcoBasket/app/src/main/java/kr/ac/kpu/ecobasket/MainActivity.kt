package kr.ac.kpu.ecobasket

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View.*
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_layout.*
import kotlinx.android.synthetic.main.header_menu.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    var database = Firebase.database.reference
    private val cabinetRef = Firebase.database.getReference("Cabinet")
    private var auth = FirebaseAuth.getInstance()
    private var usersRef = Firebase.database.getReference("users").child("${auth.currentUser?.uid}")
    //var user : User? = null  //현재 로그인한 user 정보 객체 -- 비동기식 firebase 함수 때문에 보류

    private lateinit var naverMap: NaverMap  // 네이버 지도 객체
    private lateinit var locationSource: FusedLocationSource


    @RequiresApi(Build.VERSION_CODES.M) //getColor 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*

        // 로그인 안되어있을 시에 로그인 화면
        if (Firebase.auth.currentUser == null || usersRef == null){
            startActivity(
                Intent(this, LoginActivity::class.java))
        }else{
            queryUserInformation()  //user 객체 초기화
            queryIsUsingState()
        }

         */

        queryUserInformation()  //user 객체 초기화
        queryIsUsingState()

        //위치 서비스
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        //네이버 지도
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        //우측 메뉴바
        btn_menu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.RIGHT)
            boxInfoCard.visibility = GONE   //보관함 정보 숨기기
            designButtonColor()
        }

        //메인 - 섬꾸미기 아이콘
        img_island.setOnClickListener {
            startActivity<IslandActivity>()
            boxInfoCard.visibility = GONE   //보관함 정보 숨기기
            designButtonColor()
        }

        //메인 - 우측메뉴바
        nav_menu.setNavigationItemSelectedListener(this)

        nav_menu.getHeaderView(0).setOnClickListener {
            startActivity<MyInfoActivity>()
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawers()
            }
        }
    }

    //위치 서비스 요청
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            } else {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //네이버 지도
    @RequiresApi(Build.VERSION_CODES.M)
    @UiThread
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.locationSource = locationSource

        val requirePermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, requirePermission, LOCATION_PERMISSION_REQUEST_CODE)

        map_location.map = naverMap
        map_compass.map = naverMap
        naverMap.uiSettings.isZoomControlEnabled = false

        val kpuMarker = Marker()
        kpuMarker.position = LatLng(37.34019520033833, 126.73352755632864)
        kpuMarker.map = naverMap
        kpuMarker.setOnClickListener {
            queryCabinetLocation(777, 777)
            boxInfoCard.visibility = VISIBLE
            designButtonColor()
            true
        }

        val emartMarker = Marker()
        emartMarker.position = LatLng(37.34586947498446, 126.73650149948088)
        emartMarker.map = naverMap
        emartMarker.setOnClickListener {
            queryCabinetLocation(234, 234)
            boxInfoCard.visibility = VISIBLE
            designButtonColor()
            true
        }

        val exit2Marker = Marker()
        exit2Marker.position = LatLng(37.35192338031364, 126.74285252358916)
        exit2Marker.map = naverMap
        exit2Marker.setOnClickListener {
            queryCabinetLocation(123, 123)
            boxInfoCard.visibility = VISIBLE
            designButtonColor()
            true
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
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers()
        }

        return false
    }

    //Back 버튼 이벤트 커스텀
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(Gravity.RIGHT) -> {
                drawerLayout.closeDrawers()
            }
            boxInfoCard.isVisible -> {
                boxInfoCard.visibility = GONE
                designButtonColor()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    //QRActivity 리턴값 받는 용도(대여, 반납 : requestCode로 구분)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) { //QR찍었을 때
            when(requestCode) {
                1000 -> {   //1000번 : 대여하기 QR
                    transactRemainCount(data!!.getStringExtra("qr").toString(), true)
                }
                2000 -> {   //2000번 : 반납하기 QR
                    transactRemainCount(data!!.getStringExtra("qr").toString(), false)
                }
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED) {
            toast("반납 QR 촬영 Canceled")
        }
    }

    //유저 정보 읽기 쿼리문
    private fun queryUserInformation() {
        usersRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //user = snapshot.getValue<User>()  --> Boolean값을 못받음
                val userMap = snapshot.value as Map<*, *>   //user 객체화

                //user = User()  --> DB 함수는 비동기식 함수 : 외부 변수 저장불가
                //현재 테스트용
                val userInfo = User(userMap["name"].toString(),userMap["phone"].toString(),userMap["level"].toString().toInt(),
                    userMap["mileage"].toString().toInt(), userMap["isUsing"].toString().toBoolean(), userMap["email"].toString())

                //테스트 코드 (성공 확인)
                Log.i("firebase", "Got value $userInfo")

                //회원명, 레벨 네비게이션 드로어 헤더에 적용
                tv_name_header.setText("${userInfo.name.toString()}")
                tv_level_header.setText("Lv. ${userInfo.level.toString()}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException())
            }

        })
    }

    //유저 대여 서비스 사용 여부 판단(대여/반납) 함수
    @RequiresApi(Build.VERSION_CODES.M) //getColor 함수
    private fun queryIsUsingState() {
        /** 아두이노 연결 및 구현 이후 보관함 개폐여부에 따라 대여/반납 금지하는 코드 추가 필요 */

        usersRef.child("isUsing").addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //메인 - 대여버튼 & 반납 (상태변화로 구현)
                btn_rent.setOnClickListener {
                    if(!snapshot.value.toString().toBoolean()) {
                        // QR코드리더기 액티비티 구현
                        startActivityForResult<QRActivity>(1000)
                    }
                    else {
                        startActivityForResult<QRActivity>(2000)
                    }
                }
                if(snapshot.value.toString().toBoolean()) {   //반납
                    btn_rent.text = "반납하기"
                    btn_rent.setBackgroundColor(getColor(R.color.btn_return_color))
                    btn_rent.textColor = getColor(R.color.white)
                }
                else {  //대여
                    btn_rent.text = "대여하기"
                    btn_rent.setBackgroundColor(getColor(R.color.btn_rent_color))
                    btn_rent.textColor = getColor(R.color.white)
                }
                Log.i("firebase", "Change State For IsUsing")
            }

            override fun onCancelled(error: DatabaseError) {
                //읽음 요청 실패시
                Log.e("firebase", "Error getting data")
            }

        })
    }

    //위치 데이터 읽기(Map API 후 수정)
    private fun queryCabinetLocation(locX : Int, locY : Int) {
        cabinetRef.child("${locX}_${locY}").addValueEventListener(  object : ValueEventListener {
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
                    textCabinetQR.text = "#${cabinet?.QRCode.toString()}"
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

    //QR 코드로 객체를 읽어 잔여 바구니 수 변경하는 트랜잭션
    private fun transactRemainCount(QRCode: String, isUsing : Boolean) {
        val query = cabinetRef.orderByChild("QRCode").equalTo(QRCode)
        var key : String
        query.addListenerForSingleValueEvent(object : ValueEventListener {  //QR코드로 객체 불러오기
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == null) {
                    toast("존재하지 않는 보관함 코드입니다.")
                }
                else {
                    val cabinetMap = snapshot.value as Map<String, Cabinet>
                    key = cabinetMap.keys.iterator().next()     //Location 정보 읽기
                    Log.i("Transaction", "Got Key Of $key")

                    cabinetRef.child(key).runTransaction(object : Transaction.Handler { //잔여 바구니 count
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val cabinet = mutableData.getValue<Cabinet>()
                                ?: return Transaction.success(mutableData)

                            if (isUsing) {
                                if(cabinet.remain <= 0) {   /**예외처리 필요 (구현 안됨)*/
                                    toast("현재 보관함의 잔여 바구니가 없습니다. 다른 보관함을 이용해주세요.")
                                    return Transaction.success(mutableData)
                                }
                                else {
                                    cabinet.remain -= 1
                                    usersRef.child("isUsing").setValue(true)
                                    cabinetRef.child(key).child("isOpen").setValue(true)
                                }
                            } else {                        /**예외처리 필요 (구현 안됨)*/
                                if(cabinet.remain >= MAX_COUNT_OF_BASKET) {
                                    toast("현재 보관함이 가득 찼습니다. 다른 보관함에 반납해주세요.")
                                    return Transaction.success(mutableData)
                                }
                                else {
                                    cabinet.remain += 1
                                    queryAddPoint(10)
                                    usersRef.child("isUsing").setValue(false)
                                    cabinetRef.child(key).child("isOpen").setValue(true)
                                }
                            }
                            mutableData.value = cabinet.toMap()

                            runOnUiThread {
                                val version : String = if(isUsing) "대여" else "반납"
                                alert(message = "${cabinet.name}보관함의 ${version}서비스가 완료되었습니다.") {
                                    okButton {  }
                                }.show()
                            }
                            return Transaction.success(mutableData)
                        }



                        override fun onComplete(
                            databaseError: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            Log.d("Transaction",
                                "Transaction Count Complete : $currentData"
                            )
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Transaction", "Fail To Read")
            }
        })
    }

    //에코포인트 지급 함수
    private fun queryAddPoint(point : Int) {
        usersRef.child("mileage").runTransaction(object : Transaction.Handler { //잔여 바구니 count
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val mileage = mutableData.value.toString().toInt()
                    ?: return Transaction.success(mutableData)
                mutableData.value = mileage + point
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                Log.d("Transaction",
                    "Transaction ADD Complete : $currentData"
                )
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun designButtonColor() {
        when(btn_rent.text) {
            "대여하기" -> {
                if (boxInfoCard.visibility == VISIBLE) {
                    btn_rent.setBackgroundColor(getColor(R.color.btn_rent_cardOpen_color))
                    btn_rent.textColor = getColor(R.color.black)
                } else {
                    btn_rent.setBackgroundColor(getColor(R.color.btn_rent_color))
                    btn_rent.textColor = getColor(R.color.white)
                }
            }
            "반납하기" -> {
                btn_rent.setBackgroundColor(getColor(R.color.btn_return_color))
                btn_rent.textColor = getColor(R.color.white)
            }
        }
    }
    companion object {
        const val MAX_COUNT_OF_BASKET = 12  //보관함 최대 바구니 개수
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000  // 위치 요청 코드
    }

}