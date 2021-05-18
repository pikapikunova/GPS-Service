package com.example.gpsservice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var startStopBtn: FloatingActionButton
    private lateinit var output: LinearLayout

    val listOfCoord = mutableMapOf("Кул-Шариф" to "55.798374 49.105159", "Двойка" to "55.792378 49.122228", "Деревня универсиады" to "55.7441869 49.1837646", "55.744453 49.187614" to "55.744453 49.187614")
    val APP_PREFERENCES_EDIT = "text"
    val APP_PREFERENCES_Radio = "radioGroup"
    lateinit var prefs: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startStopBtn = findViewById<FloatingActionButton>(R.id.floatingActionButton).apply{
            setOnClickListener {
                if (
                        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                ) {
                    requestLocationPermission()
                } else {
                    changeServiceState()
                }
            }
        }
        output = findViewById(R.id.coordList)
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission() {
        this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0
                && permissions.first() == Manifest.permission.ACCESS_FINE_LOCATION
                && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            changeServiceState(true)
        }
    }

    private fun sendCommand(command: String){
        val intent = Intent(this, LocationService::class.java).apply {
            this.action = command
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private val locationObserver: (Location) -> Unit = ::locationChanged

    private fun locationChanged(l: Location) {

        val tempLat = l.latitude
        val tempLong = l.longitude

        val radioGroup = findViewById<RadioGroup>(R.id.rbGroup)
        val index = radioGroup.checkedRadioButtonId
        val text = findViewById<TextView>(R.id.textView2)

        var len = 0.0
        if (index != -1)
            for ((key, value) in listOfCoord) {
                if (radioGroup.findViewById<RadioButton>(index).text == key) {
                    len = CalcLenght().calc(
                        value.split(' ')[0].toDouble(),
                        value.split(' ')[1].toDouble(),
                        tempLat,
                        tempLong
                    )

                    val tv = TextView(this@MainActivity)
                    tv.textSize = 18F
                    tv.text = getString(R.string.position, l.latitude, l.longitude)

                    val tv1 = TextView(this@MainActivity)
                    tv1.textSize = 18F
                    tv1.text = getString(R.string.lenght, len)
                    output.addView(tv)
                    output.addView(tv1)

                    text.text = len.toString()

                }
            }
        else
        {
            val tv = TextView(this@MainActivity)
            tv.textSize = 18F
            tv.text = getString(R.string.lenght, -0.0000005)
            output.addView(tv)

        }


    }

    override fun onPause() {
        super.onPause()
        val text = findViewById<TextView>(R.id.textView2)
        val radioGroup = findViewById<RadioGroup>(R.id.rbGroup)
        val editor = prefs.edit()
        editor.putString(APP_PREFERENCES_EDIT, text.text.toString()).apply()
        editor.putInt(APP_PREFERENCES_Radio, radioGroup.checkedRadioButtonId).apply()
    }

    override fun onResume() {
        super.onResume()
        val text = findViewById<TextView>(R.id.textView2)
        val radioGroup = findViewById<RadioGroup>(R.id.rbGroup)
        if (this.prefs.contains(APP_PREFERENCES_EDIT))
            text.setText(this.prefs.getString(APP_PREFERENCES_EDIT, "0"))

        if(this.prefs.contains(APP_PREFERENCES_Radio))
            radioGroup.check(this.prefs.getInt(APP_PREFERENCES_Radio, 0))
    }
        private fun changeServiceState(forceStart: Boolean = false) {
        if (!LocationService.running || forceStart) {
            sendCommand(Constants.START_LOCATION_SERVICE)
            LocationData.location.observe(this, Observer(locationObserver))

        } else {
            sendCommand(Constants.STOP_LOCATION_SERVICE)
            LocationData.location.removeObservers(this)
        }
    }


    override fun onStop() {
        super.onStop()
        LocationData.location.removeObserver(locationObserver)
    }

    override fun onStart() {
        super.onStart()
        if (LocationService.running)
            LocationData.location.observe(this, Observer(locationObserver))
    }

}