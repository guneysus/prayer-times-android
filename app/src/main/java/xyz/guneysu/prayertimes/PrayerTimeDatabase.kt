package xyz.guneysu.prayertimes

import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.room.Database
import android.arch.persistence.room.DatabaseConfiguration
import android.arch.persistence.room.InvalidationTracker
import android.arch.persistence.room.RoomDatabase


// TODO Use Singleton pattern
@Database(entities = arrayOf(PrayerTimeEntity::class), version = 1, exportSchema = false)
abstract class PrayerTimeDatabase : RoomDatabase() {
    abstract fun context(): PrayerTimeContext
}