package com.example.angelocapstoneproject

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.example.angelocapstoneproject.data.Repository
import com.example.angelocapstoneproject.domain.CallEmergencyReceiver
import com.example.angelocapstoneproject.ui.home.Home
import com.example.angelocapstoneproject.ui.home.fragments.EmergencyContactFragment
import com.example.angelocapstoneproject.ui.home.fragments.PlaybackFragment
import com.example.angelocapstoneproject.ui.splash.Splash
import com.onesignal.OneSignal
import com.onesignal.common.OneSignalUtils
import com.onesignal.common.OneSignalWrapper
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.IDisplayableMutableNotification
import com.onesignal.notifications.INotification
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.onesignal.notifications.INotificationClickResult
import com.onesignal.notifications.INotificationLifecycleListener
import com.onesignal.notifications.INotificationReceivedEvent
import com.onesignal.notifications.INotificationWillDisplayEvent
import com.onesignal.notifications.IPermissionObserver
import com.onesignal.notifications.internal.INotificationActivityOpener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

const val APP_ID = "476ef1a2-2641-4451-a445-998599b2251d"
val OneSignal.CONNECTED
    get() = "connected"

class ApplicationClass:Application() {

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "test",
                "test",
                NotificationManager.IMPORTANCE_HIGH
            )
            val mNotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.createNotificationChannel(channel)
        }*/

        OneSignal.initWithContext(this, APP_ID)
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
//        OneSignal.User.addTag("connected", "true")
        applicationScope.launch {
            OneSignal.Notifications.requestPermission(true)
        }

        OneSignal.Notifications.addClickListener(notificationListener)
    }

    private val notificationListener = object : INotificationClickListener{
        override fun onClick(event: INotificationClickEvent) {

            val data = event.notification.additionalData ?: return
            val playbackUrl = data.getString("playback_url") ?: return
            val deviceIp = data.getString("device_ip") ?: return
            val date = data.getString("date") ?: return

            if(event.result.actionId == "btn_call"){
                applicationScope.launch{
                    val repository = Repository.getInstance(applicationContext)

                    val deviceAndContact = repository.searchDeviceAndContactByIp(deviceIp)?:return@launch

                    withContext(Dispatchers.Main){
                        val intent = Intent(applicationContext, Home::class.java).also {
                            it.action = EmergencyContactFragment.EMERGENCY_NOTIF_ACTION
                            it.putExtra(PlaybackFragment.DEVICE_IP, deviceIp)
                            it.putExtra(PlaybackFragment.PLAYBACK_URL, playbackUrl)
                            it.putExtra(PlaybackFragment.DATE, date)
                            it.putExtra(PlaybackFragment.CALL_NUMBER, deviceAndContact.contact.number)
                        }
                        startActivity(intent)
                    }
                }
                return
            }

            val intent = Intent(applicationContext, Home::class.java).also {
                it.action = EmergencyContactFragment.EMERGENCY_NOTIF_ACTION
                it.putExtra(PlaybackFragment.DEVICE_IP, deviceIp)
                it.putExtra(PlaybackFragment.PLAYBACK_URL, playbackUrl)
                it.putExtra(PlaybackFragment.DATE, date)
            }
            startActivity(intent)
        }
    }

    override fun onTerminate() {
        applicationScope.cancel()
        super.onTerminate()
    }

}