package uz.mymax.million.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_playlist.view.*
import uz.mymax.million.PlaylistViewModel
import uz.mymax.million.R
import uz.mymax.million.data.Mp3Item
import uz.mymax.million.databinding.ItemPlaylistBinding

class PlaylistAdapter(
) : RecyclerView.Adapter<PlaylistAdapter.VH>() {

    private var listMp3: List<Mp3Item>? = null
    var clickListener: ((Int) -> Unit)? = null
    lateinit var viewLifecycleOwner : LifecycleOwner
    lateinit var viewModel: PlaylistViewModel


    fun updateList(newList: List<Mp3Item>) {
        this.listMp3 = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemBinding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(itemView = itemBinding.root)
    }

    override fun getItemCount() = listMp3?.size ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) {

        Log.d("AdapterTag", listMp3?.get(position).toString())
        holder.binding?.audio = listMp3?.get(position)
//        Glide.with(holder.itemView.context).load(listMp3?.get(position).image).into(holder.itemView.ima)

//            viewModel.playlist.value?.get(position)

        viewModel.playlist.value?.get(position)?.isPlaying?.observe(viewLifecycleOwner, Observer {
            Log.d("TagCheck", "Current Index: $it")
            if (it)
                holder.binding?.equalizer?.visibility = View.VISIBLE
            else
                holder.binding?.equalizer?.visibility = View.GONE
        })
        holder.itemView.setOnClickListener {
            clickListener?.let { it -> it(holder.adapterPosition) }
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = DataBindingUtil.getBinding<ItemPlaylistBinding>(itemView)
    }
}