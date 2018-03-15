package com.example.android.shushme

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.tasks.Task

class GeoFencer(private val googleApiClient: GoogleApiClient, private val context: Context){

    private var fences: MutableList<Geofence> = mutableListOf()
    val geoFences: List<Geofence> get() = fences

    fun registerGeoFences(){
        if(prerequisitesFulfilledForRegistration()) {
            try {
                val gfClient = LocationServices.getGeofencingClient(context)
                val geoFenceResult = gfClient.addGeofences(geoFencingRequest(), pendingIntentForGeoFences())

                addGeoFenceResultListeners(geoFenceResult)


            } catch (e: SecurityException) {
                Log.e(this.javaClass.simpleName, "Error in geofence request: ", e)
            }
        }
    }

    fun unregisterGeoFences(){
        if(prerequisitesFulfilledForRegistration()){
            LocationServices.getGeofencingClient(context).removeGeofences(pendingIntentForGeoFences())
        }
    }

    private fun addGeoFenceResultListeners(geoFenceResult: Task<Void>) {
        geoFenceResult.addOnSuccessListener {
            Log.d(TAG, "Connection Success!")
        }

        geoFenceResult.addOnFailureListener {
            Log.e(TAG, "Failed to register GeoFences: ", it)
        }
    }


    fun updateFencesFromStoredPlaces(places: PlaceBuffer){
        for(place in places){
            fences.add(Geofence.Builder()
                    .setRequestId(place.id)
                    .setExpirationDuration(86400000)
                    .setCircularRegion(place.latLng.latitude, place.latLng.longitude, 100f)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            )
        }
    }

//2nd Tier Functions
    fun geoFencingRequest(): GeofencingRequest{
        val request = GeofencingRequest.Builder()
    //For when you are already in the geofence
        request.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        request.addGeofences(fences)

        return request.build()
    }

    fun pendingIntentForGeoFences(): PendingIntent{
        val intentForGeoFences = Intent(context, ShushMeGeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context,0, intentForGeoFences, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun prerequisitesFulfilledForRegistration(): Boolean{
        return googleApiClient.isConnected && fences.size>0
    }

    companion object {
        val TAG = this.javaClass.simpleName
    }
}