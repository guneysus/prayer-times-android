package xyz.guneysu.prayertimes

import android.arch.persistence.room.TypeConverter
import java.time.LocalDate
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        var result = value?.let { Date(it) }
        return result
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        var result = date?.time?.toLong()
        return result
    }
}