package uz.mobilestudio.photo.models.api.all_photos


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Urls(
    @SerializedName("raw")
    val raw: String, // https://images.unsplash.com/photo-1536329583941-14287ec6fc4e?ixid=MnwyMDY1MjF8MXwxfGFsbHwxNnx8fHx8fDJ8fDE2MzMxNzYxMDk&ixlib=rb-1.2.1
    @SerializedName("full")
    val full: String, // https://images.unsplash.com/photo-1536329583941-14287ec6fc4e?crop=entropy&cs=srgb&fm=jpg&ixid=MnwyMDY1MjF8MXwxfGFsbHwxNnx8fHx8fDJ8fDE2MzMxNzYxMDk&ixlib=rb-1.2.1&q=85
    @SerializedName("regular")
    val regular: String, // https://images.unsplash.com/photo-1536329583941-14287ec6fc4e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyMDY1MjF8MXwxfGFsbHwxNnx8fHx8fDJ8fDE2MzMxNzYxMDk&ixlib=rb-1.2.1&q=80&w=1080
    @SerializedName("small")
    val small: String, // https://images.unsplash.com/photo-1536329583941-14287ec6fc4e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyMDY1MjF8MXwxfGFsbHwxNnx8fHx8fDJ8fDE2MzMxNzYxMDk&ixlib=rb-1.2.1&q=80&w=400
    @SerializedName("thumb")
    val thumb: String // https://images.unsplash.com/photo-1536329583941-14287ec6fc4e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyMDY1MjF8MXwxfGFsbHwxNnx8fHx8fDJ8fDE2MzMxNzYxMDk&ixlib=rb-1.2.1&q=80&w=200
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(raw)
        parcel.writeString(full)
        parcel.writeString(regular)
        parcel.writeString(small)
        parcel.writeString(thumb)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Urls> {
        override fun createFromParcel(parcel: Parcel): Urls {
            return Urls(parcel)
        }

        override fun newArray(size: Int): Array<Urls?> {
            return arrayOfNulls(size)
        }
    }
}