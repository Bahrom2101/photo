package uz.mobilestudio.photo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.activities.PhotoActivity
import uz.mobilestudio.photo.adpters.RvAdapter
import uz.mobilestudio.photo.databinding.FragmentPagerBinding
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.view_models.PhotosViewModel
import java.util.*
import kotlin.collections.ArrayList

class AllPhotoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    lateinit var binding: FragmentPagerBinding
    lateinit var viewModel: PhotosViewModel
    lateinit var rvAdapter: RvAdapter
    lateinit var photos: ArrayList<Photo>
    private var currentPagePhotos = 1
    private val TAG = "PagerFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPagerBinding.inflate(layoutInflater)

        photos = ArrayList()

        loadPhotos()

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rv.itemAnimator = null
        binding.rv.layoutManager = layoutManager
        binding.rv.setHasFixedSize(true)
        rvAdapter = RvAdapter(requireContext(), photos, object : RvAdapter.OnClickListener {
            override fun onPhotoClick(position: Int, photo: Photo) {
                val bundle = Bundle()
                bundle.putSerializable("photos", photos)
                bundle.putInt("position", position)
                findNavController().navigate(R.id.photoFragment, bundle)
//                val intent = Intent(requireContext(), PhotoActivity::class.java)
//                intent.putExtra("photos", photos)
//                intent.putExtra("position", position)
//                requireContext().startActivity(intent)
            }
        })
        binding.rv.adapter = rvAdapter

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.rv.canScrollVertically(1)) {
                    binding.progress2.visibility = View.VISIBLE
                    currentPagePhotos++
                    loadPhotos()
                }
            }
        })

        return binding.root
    }

    private fun loadPhotos() {
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        viewModel.getPhotos(currentPagePhotos).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.progress1.visibility = View.GONE
                binding.progress2.visibility = View.GONE
                val oldCount = photos.size
                photos.addAll(it)
                rvAdapter.notifyItemRangeInserted(oldCount, photos.size)
            }
        })
    }

}