package kr.ac.kpu.ecobasket

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.qr_overlay.*
import kotlinx.android.synthetic.main.top_action_bar_in_qr.*
import org.jetbrains.anko.toast

class QRActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qractivity)

        btn_close_qr.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btn_test_qr.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        btn_inputCode.setOnClickListener {
            /* 바코드 직접 입력 dialog 생성 */
        }

        btn_flash.setOnClickListener {
            btn_flash?.let {
                it.isSelected = !it.isSelected
            }
        }
    }


    /** MLkit 을 이용한 Android 내부 인식이 아닌 Firebase용 MLKit 사용
     * 사용 로직 이해 후에 구현필요
     */

}