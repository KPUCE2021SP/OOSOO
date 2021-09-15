package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_island.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class IslandActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var islandImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_island)

        var userThemeName : String = "island"

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        //유저의 uid를 받아서 레퍼런스 생성
        val usersRef = Firebase.database.getReference("users").child("$uid")

        //유저 데이터에 따라 섬이미지 변화(출력 리스너)
        usersRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as Map<*, *>
                val userLevel = map["level"].toString().toInt()     //레벨
                val userMileage = map["mileage"].toString().toInt()     //유저 마일리지 = 경험치
                var expPercent : Float = userMileage.toFloat() / printMaxEXP(userLevel).toFloat() * 100f   //경험치 %

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


                userEXPtextPercent.text = "${expPercent}%"
                userEXP.progress = expPercent.toInt()
            }
            override fun onCancelled(error: DatabaseError) {
                toast("DB에러")
            }
        })

        levelUpBtn.setOnClickListener {

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