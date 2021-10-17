package uz.mobilestudio.photo.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.models.api.search.SearchResponse
import uz.mobilestudio.photo.repositories.PhotosRepository
import uz.mobilestudio.photo.retrofit.Common.ACCESS_KEYS
import uz.mobilestudio.photo.retrofit.Common.PER_PAGE
import kotlin.random.Random

class PhotosViewModel : ViewModel() {

    private val photosRepository = PhotosRepository()

    fun getPhotos(page: Int): LiveData<List<Photo>> {
        return photosRepository.getPhotos(ACCESS_KEYS[Random.nextInt(0, 8)], page, PER_PAGE)
    }

    fun getPopularPhotos(page: Int): LiveData<List<Photo>> {
        return photosRepository.getPhotos(
            ACCESS_KEYS[Random.nextInt(0, 8)],
            page,
            PER_PAGE,
            "popular"
        )
    }

    fun getRandomPhoto(): LiveData<Photo> {
        return photosRepository.getRandomPhoto(ACCESS_KEYS[Random.nextInt(0, 8)])
    }


    fun getSearchedPhotos(page: Int, query: String): LiveData<SearchResponse> {
        return photosRepository.getSearchedPhotos(ACCESS_KEYS[Random.nextInt(0, 8)], page, PER_PAGE, query)
    }

}