package io.sabag.androidConferences.storage

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Entity(tableName = "conferences")
data class RoomConference(@PrimaryKey val id: String)

@Dao
interface ConferencesDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addConferences(conferencesList: List<RoomConference>)

    @Delete
    fun removeConferences(conferencesList: List<RoomConference>)

    @Query("SELECT * FROM conferences")
    fun getConferences(): List<RoomConference>
}

@Entity(tableName = "conference_details")
data class RoomConferenceDetails(
        @PrimaryKey val id: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "location") val location: String,
        @ColumnInfo(name = "start_date")val startDate: Date,
        @ColumnInfo(name = "end_date") val endDate: Date?,
        @ColumnInfo(name = "cfp_start") val cfpStart: Date?,
        @ColumnInfo(name = "cfp_end") val cfpEnd: Date?
)

@Dao
interface ConferenceDetailsDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addConferenceDetails(conferenceDetails: RoomConferenceDetails)

    @Query("SELECT * FROM conference_details")
    fun getConferenceDetails(): List<RoomConferenceDetails>
}

object TimeConverters {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?) = value?.let{ Date(value) }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?) = date?.let{date.time}
}

@Database(entities = [RoomConference::class, RoomConferenceDetails::class],
        version = 1, exportSchema = false)
@TypeConverters(TimeConverters::class)
abstract class ConferencesDatabase : RoomDatabase() {
    abstract fun conferencesDao(): ConferencesDao
    abstract fun conferenceDetailsDao(): ConferenceDetailsDao
}