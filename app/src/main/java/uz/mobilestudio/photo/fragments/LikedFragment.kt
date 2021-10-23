package uz.mobilestudio.photo.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.activities.PhotoActivity
import uz.mobilestudio.photo.activities.PhotoDbActivity
import uz.mobilestudio.photo.adpters.RvAdapterDb
import uz.mobilestudio.photo.databinding.FragmentLikedBinding
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.view_models.PhotoDbViewModel

class LikedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    lateinit var binding: FragmentLikedBinding
    lateinit var appDatabase: AppDatabase
    lateinit var rvAdapterDb: RvAdapterDb
    lateinit var viewModel: PhotoDbViewModel

    companion object {
        lateinit var photosDb: ArrayList<PhotoDb>
        var currentPhotoDbPos = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedBinding.inflate(layoutInflater)

        appDatabase = AppDatabase.getInstance(requireContext())

        photosDb = ArrayList()
        photosDb.clear()
        currentPhotoDbPos = 0
        loadPhotosDb()

        rvAdapterDb = RvAdapterDb(requireContext(), photosDb, object : RvAdapterDb.OnClickListener {
            override fun onPhotoClick(photoDb: PhotoDb, position: Int) {
                val intent = Intent(requireContext(), PhotoDbActivity::class.java)
                intent.putExtra("position", position)
                requireContext().startActivity(intent)
            }
        })
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rv.itemAnimator = null
        binding.rv.layoutManager = layoutManager
        binding.rv.setHasFixedSize(true)
        binding.rv.adapter = rvAdapterDb

        return binding.root
    }

    @SuppressLint("CheckResult")
    private fun loadPhotosDb() {
        viewModel = ViewModelProvider(this).get(PhotoDbViewModel::class.java)
        viewModel.getAllPhotoDb().observe(viewLifecycleOwner,Observer {
            if (photosDb.size > 0) {
                photosDb.clear()
            }
            val oldCount = photosDb.size
            photosDb.addAll(it)
            rvAdapterDb.notifyItemRangeInserted(oldCount, photosDb.size)
            binding.progress.visibility = View.GONE

        })
    }

    override fun onStart() {
        super.onStart()
        binding.rv.layoutManager?.scrollToPosition(currentPhotoDbPos)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.liked)
    }
}