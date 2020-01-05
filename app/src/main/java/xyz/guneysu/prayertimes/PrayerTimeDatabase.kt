package xyz.guneysu.prayertimes

import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.room.*


// TODO Use Singleton pattern
@Database(entities = arrayOf(PrayerTimeEntity::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PrayerTimeDatabase : RoomDatabase() {
    abstract fun context(): PrayerTimeContext
}