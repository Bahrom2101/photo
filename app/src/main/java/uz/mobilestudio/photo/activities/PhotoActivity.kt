package uz.mobilestudio.photo.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.view.View
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
import uz.mobilestudio.photo.databinding.ActivityPhotoBinding
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.entity.PhotoDb
import uz.mobilestudio.photo.view_models.PhotoDbViewModel
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*
import android.os.*
import uz.mobilestudio.photo.adpters.PagerPhotoAdapter
import uz.mobilestudio.photo.models.api.NetworkHelper
import uz.mobilestudio.photo.models.api.all_photos.Photo
import kotlin.collections.ArrayList

class PhotoActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhotoBinding
    lateinit var pagerPhotoAdapter: PagerPhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }

}