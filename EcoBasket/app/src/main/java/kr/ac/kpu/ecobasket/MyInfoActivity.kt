package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_info.*
import org.jetbrains.anko.toast

class MyInfoActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private var historyRef = Firebase.database.getReference("History").child("${auth.currentUser?.uid}")
    lateinit var usageRecyAdapter: UsageRecyAdapter

    val datas = mutableListOf<History>()
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        setSupportActionBar(toolbar_my_info)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecycler()

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
            }
        })

    }

    override fun onDestroy() {
        disposable?.let{ disposable!!.dispose() }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getHistory() : Observable<MutableList<History>> {

        return Observable.create { subscriber ->

            historyRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val historyList = mutableListOf<History>()
                        var historyMap = snapshot.value as Map<Long, Map<*, *>>

                        historyMap = historyMap.toSortedMap(reverseOrder())

                        historyList.apply {
                            for (i in historyMap.values.iterator()) {
                                val history = History(i["date"].toString(), i["location"].toString(), i["status"].toString().toBoolean())
                                Log.d("firebase History", "history : $history")
                                add(history)
                            }
                        }

                        subscriber.onNext(historyList)
                        subscriber.onComplete()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    subscriber.onError(error.toException())
                }
            })
        }
    }

    private fun initRecycler() {
        val divider = DividerItemDecoration(recy_usage_history.context, LinearLayoutManager(this).orientation)
        recy_usage_history.addItemDecoration(divider)

        usageRecyAdapter = UsageRecyAdapter(this)
        recy_usage_history.adapter = usageRecyAdapter

        disposable = getHistory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { historyList ->
                    Log.d("firebase History", "historyList : $historyList")
                    usageRecyAdapter.datas = historyList
                    usageRecyAdapter.notifyDataSetChanged()
                },
                { e ->
                    Log.e("firebase History", e.toString())
                },
                {
                    Log.d("firebase History", "getHistory Done")
                }
            )
    }
}