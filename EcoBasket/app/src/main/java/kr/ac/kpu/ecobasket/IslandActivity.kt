package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_island)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        //유저의 uid를 받아서 레퍼런스 생성
        val usersRef = Firebase.database.getReference("users").child("$uid")

        //데이터 한번만 알려주는 리스너
        usersRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              val map = snapshot.value as Map<*, *>

                //텍스트뷰에 텍스트 넣기
                island_name.setText(map["name"].toString())
                user_level.setText("Lv.${map["level"].toString()}")
                ecoPoint2.setText("${map["mileage"].toString()} 에코포인트")
            }
            override fun onCancelled(error: DatabaseError) {
                toast("DB에러")
            }
        })

        //테마 고르기 버튼
        shop_btn.setOnClickListener {
            startActivity<ThemeActivity>()
        }

        //뒤로가기 버튼
        btn_back4.setOnClickListener{
            finish()
        }

    }
}