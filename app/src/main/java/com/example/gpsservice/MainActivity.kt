package com.example.gpsservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var coordList: LinearLayout
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coordList = findViewById(R.id.coordList)
        button = findViewById(R.id.button)
        button.setOnClickListener{
            GPSHelper.startLocationListening(this)
            val tv = TextView(this)
            tv.text = "Широта: ${GPSHelper.imHere?.latitude}; Долгота: ${GPSHelper.imHere?.longitude}"
            coordList.addView(tv)
        }
    }
}