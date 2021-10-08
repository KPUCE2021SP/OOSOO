package kr.ac.kpu.ecobasket

import android.graphics.Color
import android.graphics.Color.WHITE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat.getColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_themes.*
import kotlinx.android.synthetic.main.activity_themes.ecoPoint3
import kr.ac.kpu.ecobasket.R.color.white
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast

class ThemeActivity : AppCompatActivity() {
    var themeName : String? = null  //테마 이름
    var auth = FirebaseAuth.getInstance()
    val usersRef = Firebase.database.getReference("users").child("${auth.currentUser?.uid}")
    val themeRef = Firebase.database.getReference("Theme").child("${auth.currentUser?.uid}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)

        val themeArray = hashMapOf<String, Button>(
            "island" to onfocused1,
            "spring" to onfocused2,
            "summer" to onfocused3,
            "autumn" to onfocused4,
            "winter" to onfocused5,
            "sunset" to onfocused6
        )

        iv_lock1.setOnClickListener {
            openTheme("summer")
        }
        iv_lock2.setOnClickListener {
            openTheme("autumn")
        }
        iv_lock3.setOnClickListener {
            openTheme("winter")
        }
        iv_lock4.setOnClickListener {
            openTheme("sunset")
        }
        //DB의 테마 정보 유저별로 초기화
        themeRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as Map<*, *>
                if(map["summer"] == true) {
                    tv_eco_lock1.visibility = View.GONE
                    iv_lock1.visibility = View.GONE
                    frame3.foreground = null
                    image_summer.isClickable = true
                    frame3.isClickable = true
                    image_summer.setOnClickListener(themeImageListener)
                }
                if(map["autumn"] == true) {
                    tv_eco_lock2.visibility = View.GONE
                    iv_lock2.visibility = View.GONE
                    frame4.foreground = null
                    image_autumn.isClickable = true
                    frame4.isClickable = true
                    image_autumn.setOnClickListener(themeImageListener)
                }
                if(map["winter"] == true) {
                    tv_eco_lock3.visibility = View.GONE
                    iv_lock3.visibility = View.GONE
                    frame5.foreground = null
                    image_winter.isClickable = true
                    frame5.isClickable = true
                    image_winter.setOnClickListener(themeImageListener)
                }
                if(map["sunset"] == true) {
                    tv_eco_lock4.visibility = View.GONE
                    iv_lock4.visibility = View.GONE
                    frame6.foreground = null
                    image_sunset.isClickable = true
                    frame6.isClickable = true
                    image_sunset.setOnClickListener(themeImageListener)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //에코포인트 데이터 표시
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as Map<*, *>
                var ecopoint = map["mileage"].toString().toInt()
                var level = map["level"].toString().toInt()

                themeName = map["theme"].toString()
                ecoPoint3.setText("$ecopoint 에코포인트")

                if(level>=3) {
                    openTheme("autumn")
                }
                if(level>=5) {
                    openTheme("autumn")
                }
                themeArray.forEach{ it ->
                    if(it.key == themeName) it.value.visibility = View.VISIBLE
                    else it.value.visibility = View.INVISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        btn_back_to_island.setOnClickListener{
            finish()
        }

        //테마 이미지에 리스너 지정
        image_island.setOnClickListener(themeImageListener)
        image_spring.setOnClickListener(themeImageListener)
    }

    //DB에 현재 설정된 테마 이름 저장하는 리스너
    private val themeImageListener= View.OnClickListener { view ->
        when (view.id) {
            R.id.image_island -> { themeName = "island" }
            R.id.image_spring -> { themeName = "spring" }
            R.id.image_summer -> { themeName = "summer" }
            R.id.image_autumn -> { themeName = "autumn" }
            R.id.image_winter -> { themeName = "winter" }
            R.id.image_sunset -> { themeName = "sunset" }
        }
        usersRef.child("theme").setValue(themeName.toString())
    }

    //열고 싶은 테마에 따라 조건 확인하는 함수
    private fun openTheme(themeName:String) {
        usersRef.runTransaction(object : Transaction.Handler { //잔여 바구니 count
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val user = mutableData.getValue<User>()
                    ?: return Transaction.success(mutableData)

                when(themeName) {
                    //여름테마 : 500포인트 소모
                    "summer" -> {
                        if(user.mileage!! >= 500) {
                            runOnUiThread{
                                alert(message = "500 포인트를 소모하여 여름테마를 구매하시겠습니까?") {
                                    okButton { usersRef.child("mileage").setValue( user.mileage!! - 500 )
                                        themeRef.child("summer").setValue(true) }
                                    cancelButton {  }
                                }.show()
                            }

                        } else {
                            runOnUiThread {
                                alert(message = "구매가능한 포인트가 부족합니다.") {
                                    okButton { }
                                }.show()
                            }
                        }
                    }
                    //가을테마 : 레벨 3이상 즉시 오픈
                    "autumn" -> {
                        if(user.level!! >= 3) {
                            themeRef.child("autumn").setValue(true)
                        }
                        else {
                            runOnUiThread{
                                alert(message = "가을테마를 열 수 있는 레벨이 낮습니다.") {
                                    okButton { }
                                }.show()
                            }
                        }
                    }
                    //겨울테마 : 800 포인트 소모
                    "winter" -> {
                        if(user.mileage!! >= 800) {
                            runOnUiThread{
                                alert(message = "800 포인트를 소모하여 겨울테마를 구매하시겠습니까?") {
                                    okButton { usersRef.child("mileage").setValue( user.mileage!! - 800 )
                                        themeRef.child("winter").setValue(true) }
                                    cancelButton {  }
                                }.show()
                            }
                        } else {
                            runOnUiThread {
                                alert(message = "구매가능한 포인트가 부족합니다.") {
                                    okButton { }
                                }.show()
                            }
                        }
                    }
                    //석양테마 : 레벨 5이상 즉시 열람
                    "sunset" -> {
                        if(user.level!! >= 5) {
                            themeRef.child("sunset").setValue(true)
                        }
                        else {
                            runOnUiThread{
                                alert(message = "석양테마를 열 수 있는 레벨이 낮습니다.") {
                                    okButton { }
                                }.show()
                            }
                        }

                    }
                }

                return Transaction.success(mutableData)
            }



            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                Log.d("Transaction",
                    "Transaction Level Up Complete : $currentData"
                )
            }
        })
    }

}