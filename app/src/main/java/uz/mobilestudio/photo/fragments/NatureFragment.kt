package uz.mobilestudio.photo.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.activities.PhotoActivity
import uz.mobilestudio.photo.adpters.RvAdapter
import uz.mobilestudio.photo.databinding.FragmentNatureBinding
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.models.api.collection.Collection
import uz.mobilestudio.photo.view_models.CollectionsResponseViewModel
import uz.mobilestudio.photo.view_models.PhotosViewModel
import java.util.*
import kotlin.collections.ArrayList

class NatureFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    lateinit var binding: FragmentNatureBinding
    lateinit var collections: ArrayList<Collection>
    lateinit var photos: ArrayList<Photo>
    lateinit var photosViewModel: PhotosViewModel
    lateinit var viewModel: CollectionsResponseViewModel
    lateinit var rvAdapter: RvAdapter
    var currentCollectionPosition = 1
    var currentPageCollections = 1
    var totalPageCollections = 1
    var currentPagePhotos = 1
    var totalPagePhotos = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNatureBinding.inflate(layoutInflater)

        photos = ArrayList()
        collections = ArrayList()

        loadCollections()

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rv.layoutManager = layoutManager
        binding.rv.setHasFixedSize(true)
        rvAdapter = RvAdapter(requireContext(), photos,object : RvAdapter.OnClickListener{
            override fun onPhotoClick(position:Int,photo: Photo) {
                val calendar = Calendar.getInstance()
                val time = calendar.time.time
                val photoDb = PhotoDb(
                    photo.id,
                    photo.width,
                    photo.height,
                    photo.urls.raw,
                    photo.urls.full,
                    photo.urls.regular,
                    photo.urls.small,
                    photo.urls.thumb,
                    time
                )
                val intent = Intent(requireContext(), PhotoActivity::class.java)
                intent.putExtra("photoDb", photoDb)
                requireContext().startActivity(intent)
            }
        })
        binding.rv.adapter = rvAdapter

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.rv.canScrollVertically(1)) {
                    binding.progress2.visibility = View.VISIBLE
                    when {
                        currentPagePhotos <= totalPagePhotos -> {
                            currentPagePhotos++
                            loadPhotos()
                        }
                        currentCollectionPosition <= collections.size -> {
                            currentCollectionPosition++
                            currentPagePhotos = 1
                            loadPhotos()
                        }
                        currentPageCollections <= totalPageCollections -> {
                            currentPageCollections++
                            currentCollectionPosition = 1
                            currentPagePhotos = 1
                            loadCollections()
                        }
                    }
                }
            }
        })

        return binding.root
    }

    private fun loadCollections() {
        viewModel = ViewModelProvider(this).get(CollectionsResponseViewModel::class.java)
        viewModel.getResponseSearchCollections(currentPageCollections, getString(R.string.nature))
            .observe(viewLifecycleOwner, Observer { collectionsResponse ->
                if (collectionsResponse != null) {
                    collections.clear()
                    totalPageCollections = collectionsResponse.totalPages
                    collections.addAll(collectionsResponse.collections)
                    totalPagePhotos = collections[currentCollectionPosition].totalPhotos / 20
                    loadPhotos()
                }
            })
    }

    private fun loadPhotos() {
        photosViewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        photosViewModel.getCollectionPhotos(
            collections[currentCollectionPosition].id,
            currentPagePhotos
        ).observe(viewLifecycleOwner, Observer {
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