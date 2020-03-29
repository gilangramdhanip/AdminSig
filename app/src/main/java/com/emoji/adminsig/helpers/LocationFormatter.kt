package com.emoji.adminsig.helpers

import android.content.Context
import android.location.Geocoder
import java.util.*


interface LocationFormatter{
    fun getLocation(context: Context,
                    latitude: Double?,
                    longitude: Double?
    ): String = try{
        val latitudeLocation = latitude ?: 0.0
        val longitudeLocation = longitude ?: 0.0
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitudeLocation, longitudeLocation,1 )
        addresses[0].getAddressLine(0)
    }catch (e: Exception){
        ""
    }
}
