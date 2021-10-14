package uz.mobilestudio.photo.adpters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import uz.mobilestudio.photo.databinding.ItemPhotoSlideBinding
import uz.mobilestudio.photo.models.api.all_photos.Photo

class SliderAdapter(var list: List<Photo>) : PagerAdapter() {

    lateinit var binding: ItemPhotoSlideBinding

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding =
            ItemPhotoSlideBinding.inflate(LayoutInflater.from(container.context), container, false)
        if (list[position].height / list[position].width >= (1).toLong()) {
            binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            binding.image.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        Glide.with(container.context).load(list[position].urls.regular).into(binding.image)
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object`  as View
        container.removeView(view)
    }
}