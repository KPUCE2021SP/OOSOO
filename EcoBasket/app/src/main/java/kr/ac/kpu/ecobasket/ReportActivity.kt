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

class ReportActivity : AppCompatActivity() {

    lateinit var spinnerAdapter : ArrayAdapter<String>
    private val dataArray = arrayOf("보관함 고장 신고", "장바구니 분실 신고")
    private var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        setSupportActionBar(toolbar_req)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tv_req_title.text = "신고하기"

        spinnerAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, dataArray)
        spinner_req.adapter = spinnerAdapter

        spinner_req.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                toast("$position 번 선택\n" +
                        "${dataArray[position]} 선택함")
                pos = position
                when (position) {
                    0 -> tv_req.text = "고장난 보관함 이름 또는 위치를 적어서 메일을 보내주세요."
                    1 -> tv_req.text = "대여한 장바구니를 분실하셨다면 대여한 보관함 위치와\n대여한 날짜를 적어서 메일을 보내주세요."
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                toast("선택되지 않음")
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