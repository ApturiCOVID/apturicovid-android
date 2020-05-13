package lv.spkc.apturicovid.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import lv.spkc.apturicovid.persistance.model.ExposureCheckToken

@Database(entities = [ExposureCheckToken::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun exposureCheckTokenDao(): ExposureCheckTokenDao

    companion object {
        private const val dbFileName = "main_database.db"
        private var INSTANCE: MainDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): MainDatabase {
            return INSTANCE ?: createInstance(context)
        }

        private fun createInstance(context: Context): MainDatabase {
            synchronized(MainDatabase::class) {
                val instance = Room
                    .databaseBuilder(context.applicationContext, MainDatabase::class.java, dbFileName)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}
