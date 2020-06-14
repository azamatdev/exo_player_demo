package uz.mymax.million.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


object DataBindingAdapter{
    @BindingAdapter("glideUrl")
    @JvmStatic fun loadImage(view: ImageView, drawable: Int?) {
        Glide.with(view.context).load(drawable).into(view)
    }
}
