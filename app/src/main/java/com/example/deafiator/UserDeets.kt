package com.example.deafiator

import android.content.Context
import android.content.SharedPreferences

object UserManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_ADMIN = "admin"
    private const val KEY_HARDWARE = "hardware"
    private const val KEY_EMERGENCY = "emergency"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_LATITUDE = "latitude"
    private const val KEY_LONGITUDE = "longitude"

    private lateinit var sharedPreferences: SharedPreferences

    // Initialize UserManager with context
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Method to store user data
    fun saveUserData(
        admin: Boolean,
        hardware: Boolean,
        emergency: Boolean,
        username: String,
        email: String,
        password: String,
        latitude: Double,
        longitude: Double
    ) {
        sharedPreferences.edit()
            .putBoolean(KEY_ADMIN, admin)
            .putBoolean(KEY_HARDWARE, hardware)
            .putBoolean(KEY_EMERGENCY, emergency)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putFloat(KEY_LATITUDE, latitude.toFloat()) // SharedPreferences supports Float
            .putFloat(KEY_LONGITUDE, longitude.toFloat()) // SharedPreferences supports Float
            .apply()
    }

    fun editEmergency(emergency: Boolean){
        sharedPreferences.edit()
            .putBoolean(KEY_EMERGENCY, emergency)
            .apply()
    }

    fun editLocation(latitude: Double, longitude: Double){
        sharedPreferences.edit()
            .putFloat(KEY_LATITUDE, latitude.toFloat())
            .putFloat(KEY_LONGITUDE, longitude.toFloat())
            .apply()
    }

    fun getLocation(): Pair<Double, Double>{
        return Pair(sharedPreferences.getFloat(KEY_LATITUDE, 0.0f).toDouble(), sharedPreferences.getFloat(KEY_LONGITUDE, 0.0f).toDouble())
    }

    // Method to retrieve user data
    fun getUserData(): Map<String, Any?> {
        return mapOf(
            "admin" to sharedPreferences.getBoolean(KEY_ADMIN, false),
            "hardware" to sharedPreferences.getBoolean(KEY_HARDWARE, false),
            "emergency" to sharedPreferences.getBoolean(KEY_EMERGENCY, false),
            "username" to sharedPreferences.getString(KEY_USERNAME, ""),
            "email" to sharedPreferences.getString(KEY_EMAIL, null),
            "password" to sharedPreferences.getString(KEY_PASSWORD, null),
            "latitude" to sharedPreferences.getFloat(KEY_LATITUDE, 0.0f).toDouble(),
            "longitude" to sharedPreferences.getFloat(KEY_LONGITUDE, 0.0f).toDouble()
        )
    }

    // Method to clear user data
    fun clearUserData() {
        sharedPreferences.edit()
            .remove(KEY_ADMIN)
            .remove(KEY_HARDWARE)
            .remove(KEY_EMERGENCY)
            .remove(KEY_USERNAME)
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .remove(KEY_LATITUDE)
            .remove(KEY_LONGITUDE)
            .apply()
    }
}