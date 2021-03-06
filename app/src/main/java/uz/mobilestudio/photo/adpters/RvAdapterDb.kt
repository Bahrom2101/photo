package uz.mobilestudio.photo.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import uz.mobilestudio.photo.databinding.ItemPhotoBinding
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.models.api.all_photos.Photo

class RvAdapterDb(val context: Context, var list: List<PhotoDb>, var onClickListener: OnClickListener) :
    RecyclerView.Adapter<RvAdapterDb.ViewHolder>() {
    inner class ViewHolder(var itemPhotoBinding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(itemPhotoBinding.root) {
        fun onBind(photoDb: PhotoDb,position: Int) {
            Glide.with(context).load(photoDb.urlSmall)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(itemPhotoBinding.image)

            itemPhotoBinding.root.setOnClickListener {
                onClickListener.onPhotoClick(photoDb,position)
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
        holder.onBind(list[position],position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onPhotoClick(photoDb: PhotoDb,position: Int)
    }
}