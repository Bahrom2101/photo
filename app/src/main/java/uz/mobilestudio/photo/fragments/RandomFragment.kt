package uz.mobilestudio.photo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
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
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.florent37.runtimepermission.kotlin.askPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.databinding.FragmentRandomBinding
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.models.NetworkHelper
import uz.mobilestudio.photo.view_models.PhotosViewModel
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*

class RandomFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    lateinit var binding: FragmentRandomBinding
    lateinit var viewModel: PhotosViewModel
    private val scope = CoroutineScope(CoroutineName("MyScope2"))
    lateinit var photoDb: PhotoDb
    lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomBinding.inflate(layoutInflater)

        binding.share.visibility = View.GONE
        binding.download.visibility = View.GONE
        binding.setBackground.visibility = View.GONE
        binding.effect.visibility = View.GONE
        binding.like.visibility = View.GONE

        appDatabase = AppDatabase.getInstance(requireContext())

        loadPhoto()

        binding.btnRandom.setOnClickListener {
            loadPhoto()
        }

        binding.share.setOnClickListener {
            onShareClick(photoDb)
        }

        binding.setBackground.setOnClickListener {
            setBack(photoDb)
        }

        binding.like.setOnClickListener {
            onLikeClick(photoDb)
        }

        binding.download.setOnClickListener {
            onDownloadClick(photoDb)
        }

        return binding.root
    }

    private fun loadPhoto() {
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        viewModel.getRandomPhoto().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.progress.visibility = View.GONE
                binding.share.visibility = View.VISIBLE
                binding.download.visibility = View.VISIBLE
                binding.setBackground.visibility = View.VISIBLE
                binding.effect.visibility = View.VISIBLE
                binding.like.visibility = View.VISIBLE
                if (it.height / it.width >= (1).toLong()) {
                    binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    binding.image.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                Glide.with(this).load(it.urls.regular).into(binding.image)
                val calendar = Calendar.getInstance()
                val time = calendar.time.time
                photoDb = PhotoDb(
                    it.id,
                    it.width,
                    it.height,
                    it.urls.raw,
                    it.urls.full,
                    it.urls.regular,
                    it.urls.small,
                    it.urls.thumb,
                    time
                )
                checkDb(photoDb.id)
            }
        })
    }

    private fun onShareClick(photoDb: PhotoDb) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Hilol Test")
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Rasmni yuklab oling: ${photoDb.urlRegular}"
        )
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    private fun setBack(photoDb: PhotoDb) {
        try {
            if (NetworkHelper(requireContext()).isNetworkConnected()) {
                scope.launch {
                    val url = URL(photoDb.urlRegular)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    val wallpaperManager =
                        WallpaperManager.getInstance(requireActivity().applicationContext)
                    wallpaperManager.setBitmap(bmp)
                }
                Toast.makeText(requireContext(), "Image had set", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show()
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
                } else {
                    binding.like.setImageResource(R.drawable.ic_like)
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

    override fun onStart() {
        super.onStart()
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.random)
    }
}