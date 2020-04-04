package uz.mymax.million.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_playlist.view.*
import uz.mymax.million.R
import uz.mymax.million.data.Mp3Item

class PlaylistAdapter(private val clickListener : (Int) -> Unit ) : RecyclerView.Adapter<PlaylistAdapter.VH>() {

    private var listMp3 : List<Mp3Item>? = null

    fun updateList ( newList : List<Mp3Item>){
        this.listMp3 = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return VH(itemView = view)
    }

    override fun getItemCount() = listMp3?.size ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) {
        listMp3?.get(position)?.let { holder.bindViews(it) }

        holder.itemView.setOnClickListener {
            clickListener(holder.adapterPosition)
        }
    }

    class VH(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bindViews(item : Mp3Item){
            itemView.audio_title.text = item.title
            itemView.audio_time_duration.text = item.duration
        }
    }
}