package com.example.gpsservice

import android.location.Location
import androidx.lifecycle.MutableLiveData

object LocationData {
    val location: MutableLiveData<Location> = MutableLiveData()
}
