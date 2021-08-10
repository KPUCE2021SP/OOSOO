package kr.ac.kpu.ecobasket

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.qr_overlay.*
import kotlinx.android.synthetic.main.top_action_bar_in_qr.*
import org.jetbrains.anko.toast

class QRActivity : AppCompatActivity() {

    var QRCode : String = ""    //QR코드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qractivity)

        btn_close_qr.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btn_test_qr.setOnClickListener { view ->
            when(testRadioGroup.checkedRadioButtonId) {
                R.id.radio123_123 -> {
                    QRCode = "1a2s3d4f"
                }
                R.id.radio234_234 -> {
                    QRCode = "as12df34"
                }
                R.id.radio777_777 -> {
                    QRCode = "abcd1234"
                }
            }
            setResult(Activity.RESULT_OK, intent)
            intent.putExtra("qr", QRCode)
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