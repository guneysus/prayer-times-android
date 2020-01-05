package xyz.guneysu.prayertimes

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import java.util.*

@Dao
interface PrayerTimeContext {
    @Query("SELECT * FROM prayer_times")
    fun getAll(): LiveData<List<PrayerTimeEntity>>

    @Query("SELECT * FROM prayer_times where uid = 1 LIMIT 1")
    fun getSample(): PrayerTimeEntity

    @Query("SELECT * FROM prayer_times WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<PrayerTimeEntity>>

//    @Query("SELECT * FROM prayer_times WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): PrayerTimeEntity

    @Query("SELECT * FROM prayer_times WHERE city = :city and :date <= date order by date LIMIT 1")
    fun get(city: String, date: Date) : PrayerTimeEntity;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg entities: PrayerTimeEntity)

    @Delete
    fun delete(entity: PrayerTimeEntity)
}