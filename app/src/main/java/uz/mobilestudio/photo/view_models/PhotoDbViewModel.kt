package uz.mobilestudio.photo.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Flowable
import uz.mobilestudio.photo.db.AppDatabase
import uz.mobilestudio.photo.entity.PhotoDb

class PhotoDbViewModel(application: Application) : AndroidViewModel(application) {

    private val appDatabase = AppDatabase.getInstance(application.applicationContext)

    fun getAllPhotoDb(): LiveData<List<PhotoDb>> {
        return appDatabase.photoDbDao().getAllPhotos()
    }

    fun deletePhotoDb(photoDb: PhotoDb): Completable {
        return appDatabase.photoDbDao().deletePhoto(photoDb)
    }

}