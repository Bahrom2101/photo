package uz.mobilestudio.photo.models.api.all_photos


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Photo(
    @SerializedName("id")
    val id: String, // lFmuWU0tv4M
    @SerializedName("width")
    val width: Long,
    @SerializedName("height")
    val height: Long,
    @SerializedName("urls")
    val urls: Urls,
) :Serializable