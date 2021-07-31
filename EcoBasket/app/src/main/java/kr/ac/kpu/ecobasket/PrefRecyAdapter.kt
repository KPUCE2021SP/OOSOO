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

    override fun onBindViewHolder(holder: PrefRecyAdapter.ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tv_item = itemView.findViewById<TextView>(R.id.tv_recy_item)

        fun bind(item: String) {
            tv_item.text = item
        }
    }

}