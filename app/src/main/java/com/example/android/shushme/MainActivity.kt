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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
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
        setupRecyclerView()
    }

//GoogleAPI
override fun onConnected(connectionHint: Bundle?) {
    Log.d(TAG, "API Connection Successful")
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
            mAdapter = PlaceListAdapter(this@MainActivity)
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



    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

}
