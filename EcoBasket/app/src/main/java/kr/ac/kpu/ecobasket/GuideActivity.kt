package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val guideAdapter = GuidePagerAdapter(getImageList())
        viewpager_guide.adapter = guideAdapter
        viewpager_guide.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        btn_next_guide.setOnClickListener {
            var num = viewpager_guide.currentItem

            if (num+1 == getImageList().size) {
                finish()
            } else {
                viewpager_guide.setCurrentItem(num+1, true)
            }
        }
    }

    private fun getImageList() : ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.google_icon, R.drawable.kakao_icon, R.drawable.ic_launcher_foreground)
    }
}