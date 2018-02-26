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

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.PlaceBuffer
import kotlinx.android.synthetic.main.item_place_card.view.*

class PlaceListAdapter(private val mContext: Context, var places: PlaceBuffer?):
        RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        // Get the RecyclerView item layout
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.item_place_card, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {

        if(places!=null){
            with(places!!.get(position)){
                holder.nameTextView.setText(name)
                holder.addressTextView.setText(address)
            }
        }
    }


    override fun getItemCount() = places?.count ?: 0

    fun swapPlaceBuffer(placeBuffer: PlaceBuffer){
        places = placeBuffer
        notifyDataSetChanged()
    }

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameTextView = itemView.name_text_view
        var addressTextView = itemView.address_text_view

    }
}
