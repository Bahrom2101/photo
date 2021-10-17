package uz.mobilestudio.photo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import uz.mobilestudio.photo.entity.PhotoDb

@Dao
interface PhotoDbDao {
    @Insert
    fun addPhoto(photoDb: PhotoDb): Completable

    @Update
    fun updatePhoto(photoDb: PhotoDb): Single<Int>

    @Delete
    fun deletePhoto(photoDb: PhotoDb): Completable

    @Query("select * from photos where id=:id")
    fun getPhotoById(id: String): Maybe<PhotoDb>

    @Query("SELECT COUNT(*) from photos where id=:id")
    fun photoCount(id: String): Single<Int>

    @Query("select * from photos order by time desc")
    fun getAllPhotos(): LiveData<List<PhotoDb>>

}