package uz.mobilestudio.photo.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import uz.mobilestudio.photo.databinding.ItemPhotoBinding
import uz.mobilestudio.photo.models.api.all_photos.Photo

class RvAdapter(val context: Context, var list: List<Photo>, var onClickListener: OnClickListener) :
    RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    inner class ViewHolder(var itemPhotoBinding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(itemPhotoBinding.root) {
        fun onBind(position: Int, photo: Photo) {
            Glide.with(context).load(photo.urls.small)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(itemPhotoBinding.image)
            itemPhotoBinding.root.setOnClickListener {
                onClickListener.onPhotoClick(position, photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position, list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onPhotoClick(position: Int, photo: Photo)
    }
}