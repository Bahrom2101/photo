package uz.mobilestudio.photo.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.activities.SearchActivity.Companion.currentPageSearchPhotos
import uz.mobilestudio.photo.activities.SearchActivity.Companion.currentSearchPos
import uz.mobilestudio.photo.activities.SearchActivity.Companion.query
import uz.mobilestudio.photo.activities.SearchActivity.Companion.searchPhotos
import uz.mobilestudio.photo.adpters.SliderRvAdapter
import uz.mobilestudio.photo.databinding.ActivitySearchPhotoBinding
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.edit.EditImageActivity
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.fragments.PopularFragment
import uz.mobilestudio.photo.models.NetworkHelper
import uz.mobilestudio.photo.view_models.PhotosViewModel
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*

class SearchPhotoActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchPhotoBinding
    lateinit var appDatabase: AppDatabase
    lateinit var viewModel: PhotosViewModel
    lateinit var sliderRvAdapter: SliderRvAdapter
    private val scope = CoroutineScope(CoroutineName("MyScope4"))
    private val TAG = "SearchPhotoActivity"

    val AD_UNIT_ID = "ca-app-pub-1320902743449340/8701300370"
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pos = intent.getIntExtra("position", 0)

        currentSearchPos = pos

        MobileAds.initialize(this) {}

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .build()
        )

        appDatabase = AppDatabase.getInstance(this)

        val calendar = Calendar.getInstance()
        val time = calendar.time.time
        var photoDb = PhotoDb(
            searchPhotos[pos].id,
            searchPhotos[pos].width,
            searchPhotos[pos].height,
            searchPhotos[pos].urls.raw,
            searchPhotos[pos].urls.full,
            searchPhotos[pos].urls.regular,
            searchPhotos[pos].urls.small,
            searchPhotos[pos].urls.thumb,
            time
        )

        checkDb(photoDb.id)

        sliderRvAdapter =
            SliderRvAdapter(this, searchPhotos, object : SliderRvAdapter.Listener {
                override fun onFinish() {
                    currentPageSearchPhotos++
                    binding.progress.visibility = View.VISIBLE
                    loadPhotos()
                }
            })
        binding.viewPager.adapter = sliderRvAdapter

        binding.viewPager.setCurrentItem(pos,false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentSearchPos = position
                photoDb = PhotoDb(
                    searchPhotos[position].id,
                    searchPhotos[position].width,
                    searchPhotos[position].height,
                    searchPhotos[position].urls.raw,
                    searchPhotos[position].urls.full,
                    searchPhotos[position].urls.regular,
                    searchPhotos[position].urls.small,
                    searchPhotos[position].urls.thumb,
                    time
                )
                checkDb(photoDb.id)
                super.onPageSelected(position)
            }
        })

        binding.share.setOnClickListener {
            onShareClick(photoDb)
        }

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
            showInterstitial()
            onDownloadClick(photoDb)
        }

        loadAd()

        binding.effect.setOnClickListener {
            val intent = Intent(this, EditImageActivity::class.java)
            intent.putExtra("photoDb",photoDb)
            startActivity(intent)
        }

    }


    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(this)
        } else {
            loadAd()
        }
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            }
        )
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

    private fun loadPhotos() {
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        viewModel.getSearchedPhotos(currentPageSearchPhotos,query)
            .observe(this, androidx.lifecycle.Observer {
                if (it != null) {
                    binding.progress.visibility = View.GONE
                    val oldCount = searchPhotos.size
                    searchPhotos.addAll(it.photos)
                    sliderRvAdapter.notifyItemRangeInserted(oldCount, searchPhotos.size)
                }
            })
    }

    private fun setBack(photoDb: PhotoDb) {
        try {
            if (NetworkHelper(this).isNetworkConnected()) {
                scope.launch {
                    val url = URL(photoDb.urlRegular)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    wallpaperManager.setBitmap(bmp)
                }
                Toast.makeText(this, "Image had set", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
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