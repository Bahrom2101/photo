package uz.mobilestudio.photo.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.retrofit.Common
import uz.mobilestudio.photo.retrofit.RetrofitService

class PhotosRepository {

    private var retrofitService: RetrofitService = Common.retrofitService()

    fun getPhotos(
        client_id: String,
        page: Int,
        per_page: Int,
    ): LiveData<List<Photo>> {
        val data = MutableLiveData<List<Photo>>()
        retrofitService.getPhotos(client_id, page, per_page)
            .enqueue(object : Callback<List<Photo>> {
                override fun onResponse(call: Call<List<Photo>>, response: Response<List<Photo>>) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<List<Photo>>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }

    fun getPhotos(
        client_id: String,
        page: Int,
        per_page: Int,
        order_by:String
    ): LiveData<List<Photo>> {
        val data = MutableLiveData<List<Photo>>()
        retrofitService.getPopularPhotos(client_id, page, per_page,order_by)
            .enqueue(object : Callback<List<Photo>> {
                override fun onResponse(call: Call<List<Photo>>, response: Response<List<Photo>>) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<List<Photo>>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }

    fun getCollectionPhotos(
        collection_id: String,
        client_id: String,
        page: Int,
        per_page: Int
    ): LiveData<List<Photo>> {
        val data = MutableLiveData<List<Photo>>()
        retrofitService.getCollectionPhotos(collection_id, client_id, page, per_page)
            .enqueue(object : Callback<List<Photo>> {
                override fun onResponse(call: Call<List<Photo>>, response: Response<List<Photo>>) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<List<Photo>>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }

    fun getSinglePhoto(photo_id:String,client_id: String):LiveData<Photo> {
        val data = MutableLiveData<Photo>()
        retrofitService.getSinglePhoto(photo_id, client_id)
            .enqueue(object : Callback<Photo> {
                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<Photo>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }

    fun getRandomPhoto(client_id: String):LiveData<Photo> {
        val data = MutableLiveData<Photo>()
        retrofitService.getRandomPhoto(client_id)
            .enqueue(object : Callback<Photo> {
                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<Photo>, t: Throwable) {
                    data.value = null
                }
            })
        return data
    }
}