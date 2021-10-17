package uz.mobilestudio.photo.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.activities.PhotoActivity
import uz.mobilestudio.photo.activities.PopularPhotoActivity
import uz.mobilestudio.photo.adpters.RvAdapter
import uz.mobilestudio.photo.databinding.FragmentPopularBinding
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.view_models.PhotosViewModel
import java.util.*
import kotlin.collections.ArrayList

class PopularFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    lateinit var binding: FragmentPopularBinding
    lateinit var viewModel: PhotosViewModel
    lateinit var rvAdapter: RvAdapter
    private val TAG = "HomeFragment1"

    companion object {
        lateinit var popularPhotos: ArrayList<Photo>
        var currentPopularPagePhotos = 1
        var currentPopularPos = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPopularBinding.inflate(layoutInflater)
        popularPhotos = ArrayList()
        popularPhotos.clear()
        currentPopularPagePhotos = 1
        currentPopularPos = 0

        loadPopularPhotos()

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadPopularPhotos()
        }

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rv.itemAnimator = null
        binding.rv.layoutManager = layoutManager
        binding.rv.setHasFixedSize(true)
        rvAdapter = RvAdapter(requireContext(), popularPhotos, object : RvAdapter.OnClickListener {
            override fun onPhotoClick(position:Int,photo: Photo) {
                val intent = Intent(requireContext(), PopularPhotoActivity::class.java)
                intent.putExtra("position", position)
                requireContext().startActivity(intent)
            }
        })
        binding.rv.adapter = rvAdapter

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.rv.canScrollVertically(1)) {
                    binding.progress2.visibility = View.VISIBLE
                    currentPopularPagePhotos++
                    loadPopularPhotos()
                }
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.rv.layoutManager?.scrollToPosition(currentPopularPos)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.popular)
    }

    private fun loadPopularPhotos() {
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        viewModel.getPopularPhotos(currentPopularPagePhotos).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.progress1.visibility = View.GONE
                binding.progress2.visibility = View.GONE
                val oldCount = popularPhotos.size
                popularPhotos.addAll(it)
                rvAdapter.notifyItemRangeInserted(oldCount, popularPhotos.size)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        })
    }

}