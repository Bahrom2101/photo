package uz.mobilestudio.photo.adpters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.fragments.*
import uz.mobilestudio.photo.models.api.all_photos.Photo

class PagerPhotoAdapter(
    var list: ArrayList<Photo>,
    var pos: Int,
    fragment: Fragment
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return PagerPhotoFragment.newInstance(list, pos+position)
    }
}