package uz.mobilestudio.photo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "photos")
data class PhotoDb(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    @ColumnInfo(name = "width")
    val width: Long,
    @ColumnInfo(name = "height")
    val height: Long,
    @ColumnInfo(name = "url_raw")
    var urlRaw: String,
    @ColumnInfo(name = "url_full")
    var urlFull: String,
    @ColumnInfo(name = "url_regular")
    var urlRegular: String,
    @ColumnInfo(name = "url_small")
    var urlSmall: String,
    @ColumnInfo(name = "url_thumb")
    var urlThumb: String,
    @ColumnInfo(name = "time")
    var time: Long
) : Serializable