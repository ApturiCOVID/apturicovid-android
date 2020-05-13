package lv.spkc.apturicovid.persistance

import androidx.lifecycle.LiveData
import androidx.room.*
import lv.spkc.apturicovid.persistance.model.ExposureCheckToken

@Dao
interface ExposureCheckTokenDao {
    @Transaction
    @Query("SELECT * FROM exposure_check_tokens WHERE is_checked = 0")
    suspend fun getUnchecked(): List<ExposureCheckToken>

    @Transaction
    @Query("SELECT * FROM exposure_check_tokens WHERE contains_exposure = 1")
    fun getExposedLiveData(): LiveData<List<ExposureCheckToken>>

    @Transaction
    @Query("SELECT * FROM exposure_check_tokens WHERE contains_exposure = 1 AND is_summary_sent = 0")
    fun getExposedNotSent(): List<ExposureCheckToken>

    @Transaction
    @Query("SELECT * FROM exposure_check_tokens WHERE id = :id")
    suspend fun findById(id: String): ExposureCheckToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exposureCheckToken: ExposureCheckToken)

    @Update
    suspend fun update(exposureCheckToken: ExposureCheckToken)

    @Transaction
    @Query("DELETE from exposure_check_tokens WHERE created_at < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}