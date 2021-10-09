package uz.mobilestudio.photo.models.api.collection


import com.google.gson.annotations.SerializedName

data class CollectionsResponse(
    @SerializedName("total")
    val total: Int, // 10000
    @SerializedName("total_pages")
    val totalPages: Int, // 10000
    @SerializedName("results")
    val collections: List<Collection>
)