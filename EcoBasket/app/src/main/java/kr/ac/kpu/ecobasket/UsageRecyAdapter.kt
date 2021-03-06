package kr.ac.kpu.ecobasket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsageRecyAdapter(private val context: Context) : RecyclerView.Adapter<UsageRecyAdapter.ViewHolder>() {
    var datas = mutableListOf<History>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageRecyAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_usage_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsageRecyAdapter.ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tv_usage_status = itemView.findViewById<TextView>(R.id.tv_usage_status)
        private val tv_usage_location = itemView.findViewById<TextView>(R.id.tv_usage_location)
        private val tv_usage_date = itemView.findViewById<TextView>(R.id.tv_usage_date)

        fun bind(item: History) {
            if (item.status == false) {
                tv_usage_status.text = "사용 중"
            } else {
                tv_usage_status.text = "반납 완료"
            }

            tv_usage_location.text = item.location
            tv_usage_date.text = item.date
        }
    }

}