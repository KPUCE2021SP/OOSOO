package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_island.*
import org.jetbrains.anko.startActivity

class IslandActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_island)

        // 이름+레벨 텍스트
        island_name.setText("지노랜드")
        user_level.setText(" Lv.100")

        //에코포인트
        ecoPoint2.setText("300" +" 에코포인트")

        //테마 고르기 버튼
        shop_btn.setOnClickListener {
            startActivity<ThemeActivity>()
        }

        //뒤로가기 버튼
        back_btn2.setOnClickListener{
            finish()
        }

    }
}