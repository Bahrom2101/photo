package uz.mobilestudio.photo.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uz.mobilestudio.photo.models.api.all_photos.Photo
import uz.mobilestudio.photo.models.api.collection.CollectionsResponse

interface RetrofitService {

    @GET("photos")
    fun getPhotos(
        @Query("client_id") client_id: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
    ): Call<List<Photo>>

    @GET("photos")
    fun getPopularPhotos(
        @Query("client_id") client_id: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("order_by") order_by: String
    ): Call<List<Photo>>

    @GET("search/collections")
    fun getResponseSearchCollection(
        @Query("client_id") client_id: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("query") query: String,
    ): Call<CollectionsResponse>

    @GET("collections/{id}/photos")
    fun getCollectionPhotos(
        @Path("id") collection_id: String,
        @Query("client_id") client_id: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ) : Call<List<Photo>>

    @GET("photos/{id}")
    fun getSinglePhoto(
        @Path("id") photo_id:String,
        @Query("client_id") client_id: String
        ): Call<Photo>

    @GET("photos/random")
    fun getRandomPhoto(
        @Query("client_id") client_id: String
        ): Call<Photo>
}