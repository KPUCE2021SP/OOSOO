package kr.ac.kpu.ecobasket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PrefRecyAdapter(private val context: Context) : RecyclerView.Adapter<PrefRecyAdapter.ViewHolder>() {
    var datas = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefRecyAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler_pref, parent, false)
        return ViewHolder(view)
    }

    // 클릭 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    // 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // SetItemClickListner로 설정한 함수 실행행
    private lateinit var itemClickListener : OnItemClickListener

    override fun onBindViewHolder(holder: PrefRecyAdapter.ViewHolder, position: Int) {
        holder.bind(datas[position])
        // 항목 클릭 시 onClick() 호출
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tv_item = itemView.findViewById<TextView>(R.id.tv_recy_item)

        fun bind(item: String) {
            tv_item.text = item
        }
    }

}