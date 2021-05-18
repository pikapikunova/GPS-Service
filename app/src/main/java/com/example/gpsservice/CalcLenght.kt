package com.example.gpsservice

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CalcLenght {

    fun calc(lat1: Double, long1: Double, lat2: Double, long2: Double): Double
    {
        val cosLat1 = cos(toRad(lat1))
        val cosLat2 = cos(toRad(lat2))
        val sinLong1 = sin(toRad(long1))
        val sinLong2 = sin(toRad(long2))
        val deltaLong = Math.abs(toRad(long2) - toRad(long1))
        val cosDelta = cos(deltaLong)
        val sinDelta = sin(deltaLong)

        val y = sqrt(Math.pow(cosLat2*sinDelta, 2.0) + Math.pow(cosLat1*sinLong2 - sinLong1*cosLat2*cosDelta, 2.0))
        val x = sinLong1*sinLong2+cosLat1*cosLat2*cosDelta

        if(Math.atan2(y, x) * 6372795<15)
            return 0.000
        else
            return Math.atan2(y, x) * 6372795
    }

    fun toRad (a:Double): Double
    {
        return (a * PI/180)
    }
}