package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.activity_themes.*
import org.jetbrains.anko.toast

class MyInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        setSupportActionBar(toolbar_my_info)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var auth = FirebaseAuth.getInstance()
        val usersRef = Firebase.database.getReference("users").child("${auth.currentUser?.uid}")

        //회원명, 레벨 불러오기
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as Map<*, *>

                tv_name.text = map["name"].toString()
                tv_level.text = "Lv. ${map["level"].toString()}"
            }
            override fun onCancelled(error: DatabaseError) {
                toast("DB에러")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }

        return super.onOptionsItemSelected(item)
    }
}