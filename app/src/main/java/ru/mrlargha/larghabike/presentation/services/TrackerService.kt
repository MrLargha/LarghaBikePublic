package ru.mrlargha.larghabike.presentation.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposable
import ru.mrlargha.larghabike.R
import ru.mrlargha.larghabike.data.util.Formatters
import ru.mrlargha.larghabike.domain.interactors.TrackingInteractor
import ru.mrlargha.larghabike.extensions.toKm
import ru.mrlargha.larghabike.extensions.toKmH
import ru.mrlargha.larghabike.presentation.ui.screens.ridehost.RideHostActivity
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import javax.inject.Inject


@AndroidEntryPoint
class TrackerService : Service() {

    companion object {
        private const val PACKAGE_NAME = "ru.mrlargha.larghabike.services.trackerservice"
        private const val ACTION_STOP_SERVICE = "$PACKAGE_NAME.startedfromnotification"
        private const val TRACKER_NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "larghabike"
        private const val NOTIFICATION_CHANNEL_NAME = "LarghaBike"

        private const val TAG = "TrackerService"

        fun getStopServiceIntent(context: Context): Intent =
            Intent(context, TrackerService::class.java)
                .putExtra(ACTION_STOP_SERVICE, true)
    }

    @Inject
    lateinit var trackingInteractor: TrackingInteractor

    @Inject
    lateinit var schedulersFacade: ISchedulersFacade

    private var trackingStarted = false

    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var trackerDataDisposable: Disposable

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.d(
                    TAG,
                    "onLocationResult: speed: ${locationResult?.lastLocation?.speed}m/s, date: ${locationResult?.lastLocation?.time}"
                )
                locationResult ?: return
                trackingInteractor.notifyLocationChanged(locationResult.lastLocation)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                trackingInteractor.notifyLocationStatusChanged(locationAvailability.isLocationAvailable)
            }
        }

        // Create a notification channel for versions higher than O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val stopRequired = intent.getBooleanExtra(ACTION_STOP_SERVICE, false)

        if (stopRequired) {
            stopTracking()
            stopSelf()
        } else if (!trackingStarted) {
            Log.i(TAG, "onStartCommand: service started and moved to FG")
            trackingStarted = true
            moveToForeground()
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create().setInterval(1000).setFastestInterval(1000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                locationCallback,
                Looper.getMainLooper()
            )

            trackingInteractor.startRide().subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui()).subscribe()

            trackerDataDisposable =
                trackingInteractor.trackerDataObservable
                    .subscribeOn(schedulersFacade.io())
                    .observeOn(schedulersFacade.ui())
                    .subscribe({
                        notificationManager.notify(
                            TRACKER_NOTIFICATION_ID,
                            getNotification(
                                it.telemetryFrame?.currentSpeed?.toKmH() ?: 0f,
                                it.statisticFrame?.totalDistance ?: 0f,
                                it.statisticFrame?.totalTime ?: 0
                            )
                        )
                    }, {
                        Log.e(TAG, "onStartCommand: observer of tracking data", it)
                    })
        }
        return START_STICKY
    }

    private fun stopTracking() {
        trackingStarted = false
        trackingInteractor.finishRide()
        trackerDataDisposable.dispose()
        notificationManager.cancel(TRACKER_NOTIFICATION_ID)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service stopped")
        stopTracking()
        super.onDestroy()
    }

    private fun getNotification(
        speed: Float = 0f,
        distance: Float = 0f,
        time: Long = 0
    ): Notification {
        val stopServiceIntent = getStopServiceIntent(this)

        val openActivityAction = NotificationCompat.Action.Builder(
            R.drawable.ic_launch,
            "Open Riding screen",
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, RideHostActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        ).build()

        val stopTrackingAction = NotificationCompat.Action.Builder(
            R.drawable.stop,
            "Stop tracking",
            PendingIntent.getService(
                this,
                0,
                stopServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        ).build()

        val notificationLayout = RemoteViews(packageName, R.layout.riding_notification)
        notificationLayout.setTextViewText(
            R.id.speed,
            Formatters.defaultNumberFormat.format(speed) + " km/h"
        )
        notificationLayout.setTextViewText(
            R.id.distance,
            Formatters.defaultNumberFormat.format(distance.toKm()) + " km"
        )
        notificationLayout.setTextViewText(R.id.time, Formatters.formatTimeHoursMinutes(time))

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .addAction(openActivityAction)
            .addAction(stopTrackingAction)
            .setCustomContentView(notificationLayout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.navigation)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun moveToForeground() {
        startForeground(TRACKER_NOTIFICATION_ID, getNotification())
    }
}