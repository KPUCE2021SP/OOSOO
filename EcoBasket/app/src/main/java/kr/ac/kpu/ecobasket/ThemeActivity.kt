package kr.ac.kpu.ecobasket

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_themes.*

class ThemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)

        //island 액티비티에서 넘어온 에코포인트 데이터를 그대로 표시

        //사용자가 테마 클릭시 '적용중'이 보이게 하고(visibility), 섬 테마 바꾸기(미구현)

        //테마 잠금 해제 조건을 달성한다면 framelayout의 foreground 속성 바꾸고, (조건 텍스트뷰, 잠금이미지)invisible
        //프레임 레이아웃 id = frame번호

        //visibility 속성 테스트
        image_island.setOnClickListener {
            onfocused1.visibility = View.VISIBLE
            onfocused2.visibility = View.INVISIBLE
            onfocused3.visibility = View.INVISIBLE
            onfocused4.visibility = View.INVISIBLE
            onfocused5.visibility = View.INVISIBLE
            onfocused6.visibility = View.INVISIBLE
        }

        image_spring.setOnClickListener {
            onfocused1.visibility = View.INVISIBLE
            onfocused2.visibility = View.VISIBLE
            onfocused3.visibility = View.INVISIBLE
            onfocused4.visibility = View.INVISIBLE
            onfocused5.visibility = View.INVISIBLE
            onfocused6.visibility = View.INVISIBLE
        }

    }
}