package uz.mobilestudio.photo.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.repositories.PhotosRepository
import uz.mobilestudio.photo.retrofit.Common.ACCESS_KEY
import uz.mobilestudio.photo.retrofit.Common.PER_PAGE

class PhotosViewModel : ViewModel() {

    private val photosRepository = PhotosRepository()

    fun getPhotos(page: Int): LiveData<List<Photo>> {
        return photosRepository.getPhotos(ACCESS_KEY, page, PER_PAGE)
    }

    fun getPopularPhotos(page: Int): LiveData<List<Photo>> {
        return photosRepository.getPhotos(ACCESS_KEY, page, PER_PAGE, "popular")
    }

    fun getCollectionPhotos(collection_id: String, page: Int): LiveData<List<Photo>> {
        return photosRepository.getCollectionPhotos(collection_id, ACCESS_KEY, page, PER_PAGE)
    }

    fun getSinglePhoto(photo_id: String): LiveData<Photo> {
        return photosRepository.getSinglePhoto(photo_id, ACCESS_KEY)
    }

    fun getRandomPhoto(): LiveData<Photo> {
        return photosRepository.getRandomPhoto(ACCESS_KEY)
    }
}