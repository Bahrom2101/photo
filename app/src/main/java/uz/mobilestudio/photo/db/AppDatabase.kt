package uz.mobilestudio.photo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.mobilestudio.photo.dao.PhotoDbDao
import uz.mobilestudio.photo.entity.PhotoDb

@Database(entities = [PhotoDb::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDbDao(): PhotoDbDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "photos_db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}