package kr.ac.kpu.ecobasket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_guide_image.view.*

class GuidePagerAdapter(imageList: ArrayList<Int>) : RecyclerView.Adapter<GuidePagerAdapter.PagerViewHolder>() {
    var item = imageList

    override fun getItemCount(): Int = item.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.image.setImageResource(item[position])
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.layout_guide_image, parent, false)){

        val image = itemView.img_guide!!
    }
}