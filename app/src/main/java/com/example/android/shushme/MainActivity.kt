package com.example.android.shushme

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.android.shushme.provider.PlaceContract
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
                    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // Member variables
    private var mAdapter: PlaceListAdapter? = null
    private lateinit var googleApiClient: GoogleApiClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeGoogleAPIClient()
        setLocationSwitchListener()
        setupRecyclerView()
        setupAddLocationListener()
    }

    override fun onResume() {
        super.onResume()
        setLocationSwitch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_PLACES && resultCode == Activity.RESULT_OK){
            val place = PlacePicker.getPlace(this, data)
            if(place==null){
                Log.e(TAG, "No Location returned from placepicker")
                return
            }

            val placeID = place.id
            val contentValues = ContentValues()
                contentValues.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeID)

            contentResolver.insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues)
            refreshPlacesIDs()

        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    //GoogleAPI
override fun onConnected(connectionHint: Bundle?) {
    Log.d(TAG, "API Connection Successful")
        refreshPlacesIDs()
}

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "API Connection Suspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "Connection to Google API Failed")
    }


//2nd Layer Functions
    private fun setupRecyclerView() {
        with(recylerview_places_main) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            mAdapter = PlaceListAdapter(this@MainActivity, null)
            adapter = mAdapter
        }
    }

    private fun makeGoogleAPIClient(){
        googleApiClient = GoogleApiClient.Builder(this).
                addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build()
    }

    private fun setLocationSwitchListener(){
        switch_location_services.setOnCheckedChangeListener{
            buttonView, isChecked ->
            if(isChecked){
                    ActivityCompat.requestPermissions(this,
                            arrayOf(ACCESS_FINE_LOCATION),
                            LOCATION_PERMISSION_REQUEST)
            }
        }
    }

    private fun setupAddLocationListener(){
            button_add_new_location.setOnClickListener{
                if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,
                            "Please Enable Location Services First",
                            Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val intentPlaces = PlacePicker.IntentBuilder().build(this)
                startActivityForResult(intentPlaces, REQUEST_PLACES)

            }
    }

    private fun setLocationSwitch(){
        val locationServicesGranted = ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        switch_location_services.isChecked = locationServicesGranted
        switch_location_services.isEnabled = !locationServicesGranted
    }

    private fun refreshPlacesIDs(){

        val currentPlaceIDsInDatabase = getPlaceIDStringList()
        //TODO ask a question about this
        if(currentPlaceIDsInDatabase!=null) {
            val pendingResult = Places.GeoDataApi.getPlaceById(googleApiClient, *currentPlaceIDsInDatabase.toTypedArray())
            pendingResult.setResultCallback {
                mAdapter?.swapPlaceBuffer(it)
            }

        }
    }

    private fun getPlaceIDStringList(): ArrayList<String>? {
        val storedPlaceIDs = contentResolver.query(PlaceContract.PlaceEntry.CONTENT_URI,
                null, null,
                null, null)
        storedPlaceIDs.moveToFirst()
        if (storedPlaceIDs.count > 0) {
            val arrayOfPlaceIDs = arrayListOf<String>()
            do {
                arrayOfPlaceIDs.add(storedPlaceIDs.getString(
                        storedPlaceIDs.getColumnIndexOrThrow(PlaceContract.PlaceEntry.COLUMN_PLACE_ID)))
            } while (storedPlaceIDs.moveToNext())

            return arrayOfPlaceIDs
        }
            return null
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        val LOCATION_PERMISSION_REQUEST = 0x4
        val REQUEST_PLACES = 449
    }

}
