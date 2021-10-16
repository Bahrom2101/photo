package uz.mobilestudio.photo.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.adpters.RvAdapter
import uz.mobilestudio.photo.databinding.ActivitySearchBinding
import uz.mobilestudio.photo.fragments.HomeFragment
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.view_models.PhotosViewModel
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    lateinit var viewModel: PhotosViewModel
    lateinit var rvAdapter: RvAdapter
    var query = ""

    companion object {
        lateinit var searchPhotos: ArrayList<Photo>
        var currentPageSearchPhotos = 1
        var currentSearchPos = 0
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchPhotos = ArrayList()
        searchPhotos.clear()
        currentPageSearchPhotos = 1
        currentSearchPos = 0

        binding.etSearch.addTextChangedListener {
            val s = it.toString()
            if (s.length > 2) {
                query = s
                loadSearchPhotos(query)
            }
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.clear.setOnClickListener {
            binding.etSearch.setText("")
        }

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rv.itemAnimator = null
        binding.rv.layoutManager = layoutManager
        binding.rv.setHasFixedSize(true)
        rvAdapter = RvAdapter(this, searchPhotos, object : RvAdapter.OnClickListener {
            override fun onPhotoClick(position: Int, photo: Photo) {
                val intent = Intent(this@SearchActivity, PhotoActivity::class.java)
                intent.putExtra("position", position)
                startActivity(intent)
            }
        })
        binding.rv.adapter = rvAdapter

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.rv.canScrollVertically(1)) {
                    binding.progress2.visibility = View.VISIBLE
                    currentPageSearchPhotos++
                    loadSearchPhotos(query)
                }
            }
        })
    }

    private fun loadSearchPhotos(query: String) {
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        viewModel.getSearchedPhotos(currentPageSearchPhotos, query).observe(this, Observer {
            if (it != null) {
                binding.progress1.visibility = View.GONE
                binding.progress2.visibility = View.GONE
                val oldCount = searchPhotos.size
                searchPhotos.addAll(it)
                rvAdapter.notifyItemRangeInserted(oldCount, searchPhotos.size)
            }
        })
    }
}