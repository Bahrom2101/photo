package uz.mobilestudio.photo.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import uz.mobilestudio.photo.databinding.ItemPhotoSlideBinding
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.models.api.all_photos.Photo
import kotlin.math.abs

class SliderDbAdapter(
    var context: Context,
    var list: List<PhotoDb>,
) :
    RecyclerView.Adapter<SliderDbAdapter.Vh>() {

    inner class Vh(var binding: ItemPhotoSlideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            if (list[position].height / list[position].width >= (1).toLong()) {
                binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                binding.image.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            Glide.with(context).load(list[position].urlRegular).into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemPhotoSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}