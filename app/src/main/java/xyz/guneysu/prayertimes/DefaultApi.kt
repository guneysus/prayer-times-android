package xyz.guneysu.prayertimes

import com.squareup.moshi.Moshi
import io.swagger.client.models.MonthlyPrayerTimes
import io.swagger.client.models.PrayerTime
import io.swagger.client.models.WeeklyPrayerTimes

public class DefaultApi {
    public fun daily(city: String) : PrayerTime {
        throw NotImplementedError()
    }

    public fun weekly(city: String) : WeeklyPrayerTimes {
        throw NotImplementedError()
    }

    public fun monthly(city: String) : MonthlyPrayerTimes {
        throw NotImplementedError()
    }
}
