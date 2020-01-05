package xyz.guneysu.prayertimes

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PrayerTimeContext {
    @Query("SELECT * FROM prayer_time")
    fun getAll(): List<PrayerTimeEntity>

    @Query("SELECT * FROM prayer_time where uid = 1 LIMIT 1")
    fun getSample(): PrayerTimeEntity

    @Query("SELECT * FROM prayer_time WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<PrayerTimeEntity>

    @Query("SELECT * FROM prayer_time WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): PrayerTimeEntity

    @Insert
    fun insertAll(vararg entities: PrayerTimeEntity)

    @Delete
    fun delete(entity: PrayerTimeEntity)
}