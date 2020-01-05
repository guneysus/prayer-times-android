package xyz.guneysu.prayertimes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.swagger.client.models.WeeklyPrayerTimes
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var moshi: Moshi
    lateinit var db: PrayerTimeDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createMoshi();
        createPrayerTimesDb()

        val today = Calendar.getInstance().time;
        val model = db.context().get("istanbul", date(today.year, today.month, today.day))

        if(model == null) updateDb(db)

        init()
    }

    private fun init() {
        val today = Calendar.getInstance().time;
        val model = db.context().get("istanbul", date(today.year, today.month, today.day))
        createNotification(model)

    }

    private fun createMoshi() {
        moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private fun createPrayerTimesDb() {
        db = Room
            .databaseBuilder(applicationContext, PrayerTimeDatabase::class.java, getString(R.string.DB_NAME))
            .allowMainThreadQueries()
            .build()
    }

    private fun createNotification(model: PrayerTimeEntity?) {


        var channelId = createNotificationChannel()

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val snoozeIntent = Intent(this, MainActivity::class.java).apply {
            action = createNotificationChannel()
            putExtra("LOREM", 0)
        }

        val updatePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

        var builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        if(model != null) {
            var content = "${model!!.fajr} ${model!!.sunrise} ${model!!.dhuhr} ${model!!.asr} ${model!!.maghrib} ${model!!.isha}"
            builder.setContentTitle("${model.city}")
                .setContentText(content)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(content)
                        .setSummaryText("${model.hijri} / ${model.gregorian}")
                        .setBigContentTitle("${model.city}")
                )
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(android.R.color.transparent)

            super.onBackPressed();
        }


        else
            builder
                .setContentText("Click update to sync prayer times")
                .setContentTitle("Connect to the internet")
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.update), updatePendingIntent)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId = 0
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel() : String {
        var channelId : String = ""

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            channelId = getString(R.string.CHANNEL_ID)

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

        return channelId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDb(db: PrayerTimeDatabase) {
// Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://api.namazvakti.guneysu.xyz/istanbul/weekly"

// Request a string response from the provided URL.
        val stringRequest = StringRequest (
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.

                var response = moshi.adapter(WeeklyPrayerTimes::class.java).fromJson(response)
                var i = 1

                for(prayerTime in response!!.data!!.iterator()) {

                    val dateRepresentation = date(prayerTime.gregorian)

                    var entity = PrayerTimeEntity(
                        uid = i,
                        city = "istanbul",
                        date = dateRepresentation,
                        fajr = prayerTime!!.fajr,
                        sunrise = prayerTime!!.sunrise,
                        dhuhr = prayerTime!!.dhuhr,
                        asr = prayerTime!!.asr,
                        maghrib = prayerTime!!.maghrib,
                        isha = prayerTime!!.isha,
                        hijri = prayerTime!!.hijri,
                        gregorian = prayerTime!!.gregorian)

                    db.context().insertAll(entity)
                    i++
                }
 
                Log.i("REQUEST_SUCCESS", "${response}")
            },
            Response.ErrorListener { err -> {
                Log.d("ERROR", err.toString(), err)
            } })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun date(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(year, month, day,0,0,0)

        cal.timeZone = TimeZone.getTimeZone("GMT")

        val dateRepresentation = cal.time

        return dateRepresentation
    }

    private fun date(greg: String) : Date {
        var splitted = greg.split(' ')
        var day = splitted.first().toInt()
        var month = when(splitted[1]) {
            "Ocak" -> 1
            "Şubat" -> 2
            "Mart" -> 3
            "Nisan" -> 4
            "Mayıs" -> 5
            "Haziran" -> 6
            "Temmuz" -> 7
            "Ağustos" -> 8
            "Eylül" -> 9
            "Ekim" -> 10
            "Kasım" -> 11
            "Aralık" -> 12
            else -> 0
        }
        var year = splitted.get(2).toInt()

        val cal = Calendar.getInstance()

        cal.set(year, month, day,0,0,0)
        cal.timeZone = TimeZone.getTimeZone("GMT")


        val dateRepresentation = cal.time
        return dateRepresentation
    }
}
