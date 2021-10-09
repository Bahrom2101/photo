package uz.mobilestudio.photo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.adpters.PagerPhotoAdapter
import uz.mobilestudio.photo.databinding.FragmentPhotoBinding
import uz.mobilestudio.photo.models.api.all_photos.Photo

class PhotoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    lateinit var binding: FragmentPhotoBinding
    lateinit var pagerPhotoAdapter: PagerPhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoBinding.inflate(layoutInflater)

        val photos = arguments?.getSerializable("photos") as ArrayList<Photo>
        val position = arguments?.getInt("position", 0) as Int

        pagerPhotoAdapter = PagerPhotoAdapter(photos,position, this)
        binding.viewPager.adapter = pagerPhotoAdapter

        return binding.root
    }
}