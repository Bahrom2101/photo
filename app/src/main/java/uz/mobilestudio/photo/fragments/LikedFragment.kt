package uz.mobilestudio.photo.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.activities.PhotoActivity
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
    lateinit var photosDb: ArrayList<PhotoDb>
    lateinit var viewModel: PhotoDbViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedBinding.inflate(layoutInflater)

        appDatabase = AppDatabase.getInstance(requireContext())

        println(44444444444444444)

        photosDb = ArrayList()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadPhotosDb()
    }

    @SuppressLint("CheckResult")
    private fun loadPhotosDb() {
        viewModel = ViewModelProvider(this).get(PhotoDbViewModel::class.java)
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            viewModel.getAllPhotoDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (photosDb.size > 0) {
                        photosDb.clear()
                    }
                    photosDb.addAll(it)
                    binding.progress.visibility = View.GONE
                    rvAdapterDb = RvAdapterDb(requireContext(), photosDb, object : RvAdapterDb.OnClickListener {
                        override fun onPhotoClick(photoDb: PhotoDb) {
                            val intent = Intent(requireContext(), PhotoActivity::class.java)
                            intent.putExtra("photoDb", photoDb)
                            requireContext().startActivity(intent)
                        }
                    })
                    val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                    binding.rv.itemAnimator = null
                    binding.rv.layoutManager = layoutManager
                    binding.rv.setHasFixedSize(true)
                    binding.rv.adapter = rvAdapterDb
                    compositeDisposable.dispose()
                }
        )
    }

    override fun onStart() {
        super.onStart()
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.liked)
    }
}