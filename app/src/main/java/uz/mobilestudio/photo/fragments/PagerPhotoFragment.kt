package uz.mobilestudio.photo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import uz.mobilestudio.photo.databinding.FragmentPagerPhotoBinding
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.view_models.PhotoDbViewModel
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PagerPhotoFragment : Fragment() {

    private var photos: ArrayList<Photo>? = null
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            photos = it.getSerializable(ARG_PARAM1) as ArrayList<Photo>
            position = it.getInt(ARG_PARAM2) as Int
        }
    }

    lateinit var binding: FragmentPagerPhotoBinding
    lateinit var appDatabase: AppDatabase
    lateinit var viewModel: PhotoDbViewModel
    private val TAG = "PhotoActivity1"
    private val scope = CoroutineScope(CoroutineName("MyScope"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPagerPhotoBinding.inflate(layoutInflater)

        appDatabase = AppDatabase.getInstance(requireContext())

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(photos: ArrayList<Photo>,position:Int) =
            PagerPhotoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, photos)
                    putInt(ARG_PARAM2, position)
                }
            }
    }
}