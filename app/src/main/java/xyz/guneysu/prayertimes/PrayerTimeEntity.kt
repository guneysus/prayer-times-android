package xyz.guneysu.prayertimes

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity(tableName = "prayer_times")
data class PrayerTimeEntity(

    @ColumnInfo(name = "date") val date: Date,

    @PrimaryKey val uid: Int,

    @ColumnInfo(name = "city") val city: kotlin.String,
    @ColumnInfo(name = "fajr") val fajr: kotlin.String,
    @ColumnInfo(name = "sunrise") val sunrise: kotlin.String,
    @ColumnInfo(name = "dhuhr") val dhuhr: kotlin.String,
    @ColumnInfo(name = "asr") val asr: kotlin.String,
    @ColumnInfo(name = "maghrib") val maghrib: kotlin.String,
    @ColumnInfo(name = "isha") val isha: kotlin.String,
    @ColumnInfo(name = "hijri") val hijri: kotlin.String,
    @ColumnInfo(name = "gregorian") val gregorian: kotlin.String
)