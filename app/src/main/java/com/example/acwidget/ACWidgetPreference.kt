package com.example.acwidget

import android.content.Context

const val AC_WIDGET_PREFERENCES = "ACWidgetPreferences"
const val KEY_IS_POWER_ON = "isPowerOn"
const val KEY_TEMPERATURE = "temperature"
const val KEY_MODE_INDEX = "modeIndex"

class ACWidgetPreference(context: Context) {
 private val prefs = context.getSharedPreferences(AC_WIDGET_PREFERENCES, Context.MODE_PRIVATE)

 var isPowerOn: Boolean
  get() = prefs.getBoolean(KEY_IS_POWER_ON, false)
  set(value) = prefs.edit().putBoolean(KEY_IS_POWER_ON, value).apply()

 var temperature: Int
  get() = prefs.getInt(KEY_TEMPERATURE, 24)
  set(value) = prefs.edit().putInt(KEY_TEMPERATURE, value).apply()

 var modeIndex: Int
  get() = prefs.getInt(KEY_MODE_INDEX, 0)
  set(value) = prefs.edit().putInt(KEY_MODE_INDEX, value).apply()
}