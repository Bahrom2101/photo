package uz.mobilestudio.photo.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.mobilestudio.photo.models.api.collection.CollectionsResponse
import uz.mobilestudio.photo.retrofit.Common
import uz.mobilestudio.photo.retrofit.RetrofitService

class CollectionsResponseRepo {

    private val retrofitService: RetrofitService = Common.retrofitService()

    fun getResponseSearchCollections(
        client_id: String,
        page: Int,
        per_page: Int,
        query: String
    ): LiveData<CollectionsResponse> {
        val data = MutableLiveData<CollectionsResponse>()
        retrofitService.getResponseSearchCollection(client_id, page, per_page, query)
            .enqueue(object : Callback<CollectionsResponse> {
                override fun onResponse(
                    call: Call<CollectionsResponse>,
                    response: Response<CollectionsResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<CollectionsResponse>, t: Throwable) {
                    data.value = null
                    println("throw -> ${t.message}")
                }
            })
        return data
    }

}