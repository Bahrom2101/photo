package uz.mobilestudio.photo.models.api.collection


import com.google.gson.annotations.SerializedName

data class Collection(
    @SerializedName("id")
    val id: String, // 3435695
    @SerializedName("title")
    val title: String, // Phone
    @SerializedName("total_photos")
    val totalPhotos: Int // 115
)