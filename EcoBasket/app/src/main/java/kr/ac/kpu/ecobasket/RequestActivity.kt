package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_request.*
import org.jetbrains.anko.email
import org.jetbrains.anko.toast

class RequestActivity : AppCompatActivity() {

    lateinit var spinnerAdapter : ArrayAdapter<String>
    private val dataArray = arrayOf("장바구니 추가 요청", "장바구니 교체 요청", "보관함 추가 설치 요청")
    private var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        setSupportActionBar(toolbar_req)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tv_req_title.text = "요청하기"

        spinnerAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, dataArray)
        spinner_req.adapter = spinnerAdapter

        spinner_req.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                pos = position
                when (position) {
                    0 -> tv_req.text = "장바구니 추가가 필요한 보관함 이름 또는 위치를 적어서\n메일을 보내주세요."
                    1 -> tv_req.text = "장바구니 교체가 필요한 보관함 이름 또는 위치와\n교체 사유를 함께 적어서 메일을 보내주세요."
                    2 -> tv_req.text = "보관함이 설치되기를 원하는 장소를 이유와 함께 적어서\n메일을 보내주세요."
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        btn_req_email.setOnClickListener {
            email("OOSOO@kpu.ac.kr", "<Eco-Basket ${dataArray[pos]}>", "${dataArray[pos]}\n")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }

        return super.onOptionsItemSelected(item)
    }
}