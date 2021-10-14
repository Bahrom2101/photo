package uz.mobilestudio.photo.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.florent37.runtimepermission.kotlin.askPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.databinding.ActivityPhotoBinding
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.view_models.PhotoDbViewModel
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*
import android.os.*
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import uz.mobilestudio.photo.adpters.SliderAdapter
import uz.mobilestudio.photo.adpters.SliderRvAdapter
import uz.mobilestudio.photo.fragments.HomeFragment
import uz.mobilestudio.photo.fragments.HomeFragment.Companion.currentPos
import uz.mobilestudio.photo.fragments.HomeFragment.Companion.photos
import uz.mobilestudio.photo.models.NetworkHelper
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.view_models.PhotosViewModel
import kotlin.collections.ArrayList

class PhotoActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhotoBinding
    lateinit var appDatabase: AppDatabase
    lateinit var viewModel: PhotosViewModel
    lateinit var sliderRvAdapter: SliderRvAdapter
    private val scope = CoroutineScope(CoroutineName("MyScope"))
    private val TAG = "HomeFragment1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "====================================")

        val pos = intent.getIntExtra("position", 0)

        currentPos = pos

        appDatabase = AppDatabase.getInstance(this)

        val calendar = Calendar.getInstance()
        val time = calendar.time.time
        var photoDb = PhotoDb(
            photos[pos].id,
            photos[pos].width,
            photos[pos].height,
            photos[pos].urls.raw,
            photos[pos].urls.full,
            photos[pos].urls.regular,
            photos[pos].urls.small,
            photos[pos].urls.thumb,
            time
        )

        checkDb(photoDb.id)

        sliderRvAdapter =
            SliderRvAdapter(this, photos, pos, object : SliderRvAdapter.Listener {
                override fun onFinish() {
                    HomeFragment.currentPagePhotos++
                    binding.progress.visibility = View.VISIBLE
                    loadPhotos()
                }
            })
        binding.viewPager.adapter = sliderRvAdapter

        binding.viewPager.setCurrentItem(pos,false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPos = position
                photoDb = PhotoDb(
                    photos[position].id,
                    photos[position].width,
                    photos[position].height,
                    photos[position].urls.raw,
                    photos[position].urls.full,
                    photos[position].urls.regular,
                    photos[position].urls.small,
                    photos[position].urls.thumb,
                    time
                )
                checkDb(photoDb.id)
                super.onPageSelected(position)
            }
        })

        binding.setBackground.setOnClickListener {
            setBack(photoDb)
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.like.setOnClickListener {
            onLikeClick(photoDb)
        }

        binding.download.setOnClickListener {
            onDownloadClick(photoDb)
        }

    }

    private fun loadPhotos() {
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        viewModel.getPhotos(HomeFragment.currentPagePhotos)
            .observe(this, androidx.lifecycle.Observer {
                if (it != null) {
                    binding.progress.visibility = View.GONE
                    val oldCount = photos.size
                    photos.addAll(it)
                    sliderRvAdapter.notifyItemRangeInserted(oldCount, photos.size)
                }
            })
    }

    private fun setBack(photoDb: PhotoDb) {
        try {
            if (NetworkHelper(this@PhotoActivity).isNetworkConnected()) {
                scope.launch {
                    val url = URL(photoDb.urlRegular)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    wallpaperManager.setBitmap(bmp)
                }
                Toast.makeText(this@PhotoActivity, "Image had set", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@PhotoActivity, "No Internet", Toast.LENGTH_SHORT).show()
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
            if (NetworkHelper(this).isNetworkConnected()) {
                val url = photoDb.urlRegular
                val downloadManager =
                    getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            }
        }.onDeclined { e ->
            println(3)
            if (e.hasDenied()) {
                AlertDialog.Builder(this)
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

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {})
    }
}