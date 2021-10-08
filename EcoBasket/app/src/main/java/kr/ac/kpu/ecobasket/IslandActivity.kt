package kr.ac.kpu.ecobasket

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_island.*
import org.jetbrains.anko.*

class IslandActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid

    //유저의 uid를 받아서 레퍼런스 생성
    private val usersRef = Firebase.database.getReference("users").child("$uid")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_island)

        var userThemeName : String? = null

        //유저 데이터에 따라 섬이미지 변화(출력 리스너)
        usersRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as Map<*, *>
                val userLevel = map["level"].toString().toInt()     //레벨
                val userMileage = map["mileage"].toString().toInt()     //유저 마일리지 = 경험치
                var expPercent : Float = userMileage.toFloat() / printMaxEXP(userLevel).toFloat() * 100f   //경험치 %
                userThemeName = map["theme"].toString()


                if(expPercent >= 100 || printMaxEXP(userLevel) == 0) {
                    expPercent = 100f
                    expPercent = String.format("%.1f",expPercent).toFloat()
                } else expPercent = String.format("%.1f",expPercent).toFloat()  //경험치 % 조정

                userEXPtextRatio.text = "$userMileage / ${printMaxEXP(userLevel)}"

                //메인 섬 이미지 레벨별 이미지 소스 변경
                main_island.setImageResource(resources.getIdentifier("@drawable/${userThemeName}_lv$userLevel",null,packageName))

                //텍스트뷰에 텍스트 넣기
                island_name.text = map["name"].toString() + "의 섬"
                user_level.text = "Lv.$userLevel"
                ecoPoint.text = "$userMileage 에코포인트"

                //userName 하이라이트 색 변경
                val island_string = island_name.text.toString()
                val builder = SpannableStringBuilder(island_string)
                val colorBlueSpan = ForegroundColorSpan(getColor(R.color.island_main_text_color))
                builder.setSpan(colorBlueSpan, island_string.length - 3, island_string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                island_name.text = builder

                userEXPtextPercent.text = "${expPercent}%"
                userEXP.progress = expPercent.toInt()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        levelUpBtn.setOnClickListener {
            levelUp()
        }
        //테마 고르기 버튼
        shop_btn.setOnClickListener {
            startActivity<ThemeActivity>()
        }

        //뒤로가기 버튼
        btn_back_to_main.setOnClickListener{
            finish()
        }

    }

    private fun levelUp() {
        usersRef.runTransaction(object : Transaction.Handler { //잔여 바구니 count
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val user = mutableData.getValue<User>()
                    ?: return Transaction.success(mutableData)

                if(user.mileage!! >= printMaxEXP(user.level!!)) {
                    usersRef.child("mileage").setValue( user.mileage!! - printMaxEXP(user.level!!) )
                    usersRef.child("level").setValue( user.level!! + 1 )
                }
                else {
                    runOnUiThread {
                        toast("경험치가 부족합니다. 마일리지를 모아주세요.")
                    }
                    return Transaction.success(mutableData)
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
    private fun printMaxEXP(level : Int) : Int{
        return when (level) {
            1 -> 100
            2 -> 150
            3 -> 200
            4 -> 250
            5 -> 300
            6 -> 350
            7 -> 400
            else -> 0
        }
    }
}