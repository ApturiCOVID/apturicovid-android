package lv.spkc.apturicovid.ui.statistics

import android.content.Context
import com.google.gson.GsonBuilder
import lv.spkc.apturicovid.di.module.NetworkModule
import lv.spkc.apturicovid.network.CovidStats
import lv.spkc.apturicovid.network.DateTimeDeserializer
import lv.spkc.apturicovid.network.FileLoader
import org.joda.time.DateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(private val fileLoader: FileLoader) {
    companion object {
        private const val TEMP_STATS_FILE_PREFIX = "stats"
        private val GSON = GsonBuilder().registerTypeAdapter(DateTime::class.java, DateTimeDeserializer()).create()
    }

    suspend fun getStats(context: Context): CovidStats? {
        return fileLoader.getJsonFromApiFile(context, NetworkModule.STATS_URL, TEMP_STATS_FILE_PREFIX)?.let {
            GSON.fromJson(it, CovidStats::class.java)
        }
    }
}