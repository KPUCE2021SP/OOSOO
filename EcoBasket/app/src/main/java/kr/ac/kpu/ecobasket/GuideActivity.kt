package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val guideAdapter = GuidePagerAdapter(getImageList(), getTextList())
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

        viewpager_guide.setPageTransformer(IndicatorPageTransformer())

        indicator0_guide.setOnClickListener {
            viewpager_guide.currentItem = 0
        }
        indicator1_guide.setOnClickListener {
            viewpager_guide.currentItem = 1
        }
        indicator2_guide.setOnClickListener {
            viewpager_guide.currentItem = 2
        }
        indicator3_guide.setOnClickListener {
            viewpager_guide.currentItem = 3
        }
    }

    private fun getImageList() : ArrayList<ArrayList<Int>> {

        var page1 = arrayListOf<Int>(R.drawable.marker, R.drawable.qr_scan, R.drawable.eco_basket_icon, R.drawable.storage_lock)
        var page2 = arrayListOf<Int>(R.drawable.error, R.drawable.request_icon, R.drawable.solve_icon)
        var page3 = arrayListOf<Int>(R.drawable.marker, R.drawable.qr_scan, R.drawable.return_icon, R.drawable.storage_lock)
        var page4 = arrayListOf<Int>(R.drawable.eco_point_64px, R.drawable.theme_change_icon, R.drawable.protect_icon)

        return arrayListOf<ArrayList<Int>>(page1, page2, page3, page4)
    }

    private fun getTextList() : ArrayList<ArrayList<String>> {

        var page1 = arrayListOf<String>(
            "내 주변에 있는 보관함을 찾아보세요",
            "보관함에 부착된 QR코드를 스캔합니다",
            "보관함을 열어 장바구니를 챙겨주세요",
            "보관함 뚜껑을 닫아주세요")

        var page2 = arrayListOf<String>(
            "장바구니를 사용하다 문제가 생겼을 시에",
            "우측 상단 메뉴바에 환경설정-요청하기, 신고하기로 문제 상황을 알려주시면",
            "문제를 즉각 해결해드리겠습니다")

        var page3 = arrayListOf<String>(
            "대여하신 장바구니를 들고 주변에 있는 보관함을 찾아보세요",
            "보관함에 부착된 QR코드를 스캔합니다",
            "보관함을 열어 장바구니를 반납해주세요",
            "보관함 뚜껑을 닫아주세요.")

        var page4 = arrayListOf<String>(
            "정상적으로 Eco Basket을 이용해 주셨다면 에코포인트가 쌓입니다",
            "쌓인 포인트로 다른 테마로 바꿀 수 있습니다",
            "환경보호에 이바지해 주셔서 감사합니다")

        return arrayListOf<ArrayList<String>>(page1, page2, page3, page4)
    }

    inner class IndicatorPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            indicator0_guide.setImageResource(R.drawable.shape_circle_gray)
            indicator1_guide.setImageResource(R.drawable.shape_circle_gray)
            indicator2_guide.setImageResource(R.drawable.shape_circle_gray)
            indicator3_guide.setImageResource(R.drawable.shape_circle_gray)
            when (viewpager_guide.currentItem) {
                0 -> {
                    indicator0_guide.setImageResource(R.drawable.shape_circle_green)
                }
                1 -> {
                    indicator1_guide.setImageResource(R.drawable.shape_circle_green)
                }
                2 -> {
                    indicator2_guide.setImageResource(R.drawable.shape_circle_green)
                }
                3 -> {
                    indicator3_guide.setImageResource(R.drawable.shape_circle_green)
                }
            }
        }

    }
}