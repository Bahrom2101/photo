package uz.mobilestudio.photo.models.api.search


import com.google.gson.annotations.SerializedName
import uz.mobilestudio.photo.models.api.all_photos.Photo

data class SearchResponse(
    @SerializedName("total")
    val total: Int, // 10000
    @SerializedName("total_pages")
    val totalPages: Int, // 10000
    @SerializedName("results")
    val photos: List<Photo>
)