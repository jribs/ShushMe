package com.example.android.shushme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class ShushMeGeofenceBroadcastReceiver : BroadcastReceiver(){

    companion object {
        val TAG = this.javaClass.simpleName
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context!=null) {
            val event = GeofencingEvent.fromIntent(intent)

            when (event.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> setRingerMode(context, AudioManager.RINGER_MODE_SILENT)
                Geofence.GEOFENCE_TRANSITION_EXIT -> setRingerMode(context, AudioManager.RINGER_MODE_NORMAL)
                else -> Log.e(TAG, "Unknown GeoFence Transition: ${event.geofenceTransition}")
            }
        }
    }

    private fun setRingerMode(context: Context, mode: Int){
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT <24 || (android.os.Build.VERSION.SDK_INT >= 24 && !notifManager.isNotificationPolicyAccessGranted)){
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.ringerMode = mode
        }
    }

    private fun sendNotification(context: Context, geofenceTransitionType: Int){
        val notifBuilder = NotificationCompat.Builder(context, NotificationChannel.DEFAULT_CHANNEL_ID)


            if(geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
                with(notifBuilder) {
                    setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                    setLargeIcon(BitmapFactory.decodeResource(context.resources,
                            R.drawable.ic_volume_off_white_24dp))
                    setContentTitle(context.getString(R.string.silent_mode_on))
                }
            }
            else if (geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
                with(notifBuilder){
                    setSmallIcon(R.drawable.ic_volume_up_white_24dp)
                    setLargeIcon(BitmapFactory.decodeResource(context.resources,
                            R.drawable.ic_volume_up_white_24dp))
                    setContentTitle(context.getString(R.string.silent_mode_off))
                }
            }
        }
    
}