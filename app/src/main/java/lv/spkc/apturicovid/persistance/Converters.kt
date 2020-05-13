package lv.spkc.apturicovid.persistance

import androidx.room.TypeConverter
import org.joda.time.DateTime

class Converters {
    companion object {
        const val MILLIS_PER_SECOND = 1000
    }

    // Let's use seconds when storing datetime in the database
    @TypeConverter
    fun fromTimestamp(value: Long?): DateTime? {
        return value?.let {
            DateTime(it * MILLIS_PER_SECOND)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: DateTime?): Long? {
        return date?.let {
            it.millis / MILLIS_PER_SECOND
        }
    }
}
