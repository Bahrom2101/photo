package uz.mobilestudio.photo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.florent37.runtimepermission.kotlin.askPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.databinding.FragmentPagerPhotoBinding
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.models.api.NetworkHelper
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.view_models.PhotoDbViewModel
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*
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

//        val photoDb = requireActivity().intent.getSerializableExtra("photoDb") as PhotoDb

        println(position)
        println(photos)

        val calendar = Calendar.getInstance()
        val time = calendar.time.time
        val photoDb = PhotoDb(
            photos!![position!!].id,
            photos!![position!!].width,
            photos!![position!!].height,
            photos!![position!!].urls.raw,
            photos!![position!!].urls.full,
            photos!![position!!].urls.regular,
            photos!![position!!].urls.small,
            photos!![position!!].urls.thumb,
            time
        )

        checkDb(photoDb.id)

        doInit(photoDb)

        binding.setBackground.setOnClickListener {
            setBack(photoDb)
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.like.setOnClickListener {
            onLikeClick(photoDb)
        }

        binding.download.setOnClickListener {
            onDownloadClick(photoDb)
        }
        return binding.root
    }

    private fun doInit(photoDb: PhotoDb) {
        binding.progress.visibility = View.GONE
        binding.share.visibility = View.VISIBLE
        binding.download.visibility = View.VISIBLE
        binding.setBackground.visibility = View.VISIBLE
        binding.effect.visibility = View.VISIBLE
        binding.like.visibility = View.VISIBLE
        if (photoDb.height / photoDb.width >= (1).toLong()) {
            binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            binding.image.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        Glide.with(this).load(photoDb.urlRegular).into(binding.image)

    }

    private fun setBack(photoDb: PhotoDb) {
        try {
            scope.launch {
                if (NetworkHelper(requireContext()).isNetworkConnected()) {
                    val url = URL(photoDb.urlRegular)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    val wallpaperManager = WallpaperManager.getInstance(requireActivity().applicationContext)
                    wallpaperManager.setBitmap(bmp)
                    Toast.makeText(requireContext(), "Image had set", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("CheckResult")
    private fun checkDb(id: String) {
        appDatabase.photoDbDao().photoCount(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == 1) {
                    binding.like.setImageResource(R.drawable.like)
                }
            }, {

            })
    }

    private fun onDownloadClick(photoDb: PhotoDb) {
        askPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            if (NetworkHelper(requireContext()).isNetworkConnected()) {
                val url = photoDb.urlRegular
                val downloadManager =
                    requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uri = Uri.parse(url)
                val request = DownloadManager.Request(uri)
                request.setNotificationVisibility((DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED))
                val destinationDirectory = Environment.DIRECTORY_PICTURES
                request.setDestinationInExternalPublicDir(
                    destinationDirectory,
                    getString(R.string.app_name) + File.separator + photoDb.id + ".jpg"
                )
                val enqueue = downloadManager.enqueue(request)
            } else {
                Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show()
            }
        }.onDeclined { e ->
            println(3)
            if (e.hasDenied()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Please accept our permissions")
                    .setPositiveButton("yes") { _, _ ->
                        e.askAgain();
                    } //ask again
                    .setNegativeButton("no") { dialog, _ ->
                        dialog.dismiss();
                    }
                    .show();
            }

            if (e.hasForeverDenied()) {
                e.goToSettings();
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun onLikeClick(photoDb: PhotoDb) {
        appDatabase.photoDbDao().getPhotoById(photoDb.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                appDatabase.photoDbDao().deletePhoto(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        binding.like.setImageResource(R.drawable.ic_like)
                    }, {

                    })
            }, {

            }, {
                addPhotoToDb(photoDb)
            })
    }

    @SuppressLint("CheckResult")
    private fun addPhotoToDb(photoDb: PhotoDb) {
        val calendar = Calendar.getInstance()
        val time = calendar.time.time
        photoDb.time = time
        appDatabase.photoDbDao().addPhoto(photoDb)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.like.setImageResource(R.drawable.like)
            }, {})
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