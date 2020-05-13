package lv.spkc.apturicovid.persistance.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "exposure_check_tokens")
data class ExposureCheckToken(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",

    @ColumnInfo(name = "contains_exposure")
    var containsExposure: Boolean = false,

    @ColumnInfo(name = "is_checked")
    var isChecked: Boolean = false,

    @ColumnInfo(name = "is_summary_sent")
    var isSummarySent: Boolean = false,

    @ColumnInfo(name = "created_at")
    var createdAt: DateTime = DateTime()
)