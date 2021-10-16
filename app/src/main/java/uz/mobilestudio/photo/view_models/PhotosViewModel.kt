package uz.mobilestudio.photo.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.repositories.PhotosRepository
import uz.mobilestudio.photo.retrofit.Common.ACCESS_KEY1
import uz.mobilestudio.photo.retrofit.Common.ACCESS_KEY2
import uz.mobilestudio.photo.retrofit.Common.ACCESS_KEY3
import uz.mobilestudio.photo.retrofit.Common.PER_PAGE
import kotlin.random.Random

class PhotosViewModel : ViewModel() {

    private val photosRepository = PhotosRepository()

    fun getPhotos(page: Int): LiveData<List<Photo>> {
        return when (Random.nextInt(1, 4)) {
            1 -> {
                photosRepository.getPhotos(ACCESS_KEY1, page, PER_PAGE)
            }
            2 -> {
                photosRepository.getPhotos(ACCESS_KEY2, page, PER_PAGE)
            }
            3 -> {
                photosRepository.getPhotos(ACCESS_KEY3, page, PER_PAGE)
            }
            else -> {
                photosRepository.getPhotos(ACCESS_KEY3, page, PER_PAGE)
            }
        }
    }

    fun getPopularPhotos(page: Int): LiveData<List<Photo>> {
        return when (Random.nextInt(1, 4)) {
            1 -> {
                photosRepository.getPhotos(ACCESS_KEY1, page, PER_PAGE, "popular")
            }
            2 -> {
                photosRepository.getPhotos(ACCESS_KEY2, page, PER_PAGE, "popular")
            }
            3 -> {
                photosRepository.getPhotos(ACCESS_KEY3, page, PER_PAGE, "popular")
            }
            else -> {
                photosRepository.getPhotos(ACCESS_KEY3, page, PER_PAGE, "popular")
            }
        }
    }

    fun getRandomPhoto(): LiveData<Photo> {
        return when (Random.nextInt(1, 4)) {
            1 -> {
                photosRepository.getRandomPhoto(ACCESS_KEY1)
            }
            2 -> {
                photosRepository.getRandomPhoto(ACCESS_KEY2)
            }
            3 -> {
                photosRepository.getRandomPhoto(ACCESS_KEY3)
            }
            else -> {
                photosRepository.getRandomPhoto(ACCESS_KEY3)
            }
        }
    }


    fun getSearchedPhotos(page: Int,query:String): LiveData<List<Photo>> {
        return when (Random.nextInt(1, 4)) {
            1 -> {
                photosRepository.getSearchedPhotos(ACCESS_KEY1, page, PER_PAGE, query)
            }
            2 -> {
                photosRepository.getSearchedPhotos(ACCESS_KEY2, page, PER_PAGE, query)
            }
            3 -> {
                photosRepository.getSearchedPhotos(ACCESS_KEY3, page, PER_PAGE, query)
            }
            else -> {
                photosRepository.getSearchedPhotos(ACCESS_KEY3, page, PER_PAGE, query)
            }
        }
    }

}