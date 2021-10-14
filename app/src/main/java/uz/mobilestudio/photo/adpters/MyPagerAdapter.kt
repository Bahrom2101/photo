package uz.mobilestudio.photo.adpters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(
    fragment: Fragment
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position + 1) {
            1 -> {
                AllPhotoFragment()
            }
            2 -> {
                AnimalsFragment()
            }
            3 -> {
                TechnologyFragment()
            }
            4 -> {
                NatureFragment()
            }
            else -> AllPhotoFragment()
        }
    }
}