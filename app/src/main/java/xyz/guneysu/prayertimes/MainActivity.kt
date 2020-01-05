package xyz.guneysu.prayertimes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.moshi.Moshi
import io.swagger.client.models.PrayerTime

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = createPrayerTimesDb();
        simpleRequest(db)
    }

    private fun createPrayerTimesDb() : PrayerTimeDatabase {
        val db = Room.databaseBuilder(
            applicationContext,
            PrayerTimeDatabase::class.java, "prayer-times"
        )
            .build()
        return db
    }

    private fun createNotification(model: PrayerTimeEntity) {

        var content = "${model.fajr} ${model.sunrise} ${model.dhuhr} ${model.asr} ${model.maghrib} ${model.isha}";

        var channelId = createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val snoozeIntent = Intent(this, MainActivity::class.java).apply {
            action = createNotificationChannel()
            putExtra("LOREM", 0)
        }

        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

        var builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Prayer Times")
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
                    .setSummaryText(model.city)
                    .setBigContentTitle("Prayer Times")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
//            .addAction(
//                R.drawable.ic_launcher_foreground, getString(R.string.snooze),
//                snoozePendingIntent
//            )
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(android.R.color.transparent);

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId = 0
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel() : String {
        var channelId : String = "";

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

        return channelId;
    }

    private fun simpleRequest(db: PrayerTimeDatabase) {
// Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://virtserver.swaggerhub.com/guneysus/prayer-times/v1/istanbul/daily"

// Request a string response from the provided URL.
        val stringRequest = StringRequest (
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                var prayerTime = Moshi.Builder().build().adapter(PrayerTime::class.java).fromJson(response)
                var entity = PrayerTimeEntity(
                    uid = 1,
                    city = "istanbul",
                    fajr = prayerTime!!.fajr,
                    sunrise = prayerTime!!.sunrise,
                    dhuhr = prayerTime!!.dhuhr,
                    asr = prayerTime!!.asr,
                    maghrib = prayerTime!!.maghrib,
                    isha = prayerTime!!.isha,
                    hijri = prayerTime!!.hijri,
                    gregorian = prayerTime!!.gregorian)

                db.context().insertAll(entity)

                val model = db.context().getSample()

                createNotification(model)
 
                Log.i("REQUEST_SUCCESS", "${response}")
            },
            Response.ErrorListener { err -> {
                Log.d("ERROR", err.toString(), err)
            } })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}
