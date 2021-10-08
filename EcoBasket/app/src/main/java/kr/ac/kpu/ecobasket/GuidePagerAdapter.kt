package kr.ac.kpu.ecobasket

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_guide_image.view.*

class GuidePagerAdapter(imageList: ArrayList<ArrayList<Int>>, textList: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<GuidePagerAdapter.PagerViewHolder>() {
    var images = imageList
    var texts = textList

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        if (images[position].size == 3) {
            holder.imageViews[3].isGone = true
            holder.textViews[3].isGone = true
        }

        for (i in 0 until images[position].size) {
            holder.imageViews[i].setImageResource(images[position][i])
            holder.textViews[i].setText(texts[position][i])
        }
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.layout_guide_image, parent, false)){

        val imageViews = arrayListOf<ImageView>(itemView.img_guide1, itemView.img_guide2, itemView.img_guide3, itemView.img_guide4)
        val textViews = arrayListOf<TextView>(itemView.tv_guide1, itemView.tv_guide2, itemView.tv_guide3, itemView.tv_guide4)
    }
}