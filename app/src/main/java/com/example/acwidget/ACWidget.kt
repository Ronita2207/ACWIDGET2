package com.example.acwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.widget.RemoteViews
import android.content.ComponentName
import android.util.Log

const val TOGGLE_POWER_ACTION = "com.example.acwidget.TOGGLE_POWER"
const val CHANGE_MODE_ACTION = "com.example.acwidget.CHANGE_MODE"
const val UPDATE_WIDGET_ACTION = "com.example.acwidget.UPDATE_WIDGET"

class ACWidget : AppWidgetProvider() {
    private val TAG = "ACWidget"
    private val modes = listOf("Auto", "Cool", "Dry", "Fan")

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate called")
        updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive called with action: ${intent.action}")
        when (intent.action) {
            TOGGLE_POWER_ACTION -> handleTogglePower(context)
            CHANGE_MODE_ACTION -> changeMode(context)
            UPDATE_WIDGET_ACTION, AppWidgetManager.ACTION_APPWIDGET_UPDATE -> updateAllWidgets(context)
        }
    }

    private fun handleTogglePower(context: Context) {
        Log.d(TAG, "handleTogglePower called")
        val prefs = ACWidgetPreference(context)
        prefs.isPowerOn = !prefs.isPowerOn
        updateAllWidgets(context)
    }

    private fun changeMode(context: Context) {
        Log.d(TAG, "handleTogglePower called")
        val prefs = ACWidgetPreference(context)
        prefs.modeIndex = (prefs.modeIndex + 1) % modes.size
        updateAllWidgets(context)
    }

    private fun updateAllWidgets(context: Context) {
        Log.d(TAG, "updateAllWidgets called")
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, ACWidget::class.java))
        Log.d(TAG, "Updating ${appWidgetIds.size} widgets")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun getPowerTogglePendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        val intent = Intent(context, ACWidget::class.java).apply {
            action = TOGGLE_POWER_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getModeTogglePendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        val intent = Intent(context, ACWidget::class.java).apply {
            action = CHANGE_MODE_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        Log.d(TAG, "updateAppWidget called for widget ID: $appWidgetId")
        val views = RemoteViews(context.packageName, R.layout.a_c_widget)
        val prefs = ACWidgetPreference(context)

        val isPowerOn = prefs.isPowerOn
        val temperature = prefs.temperature
        val modeIndex = prefs.modeIndex

        views.setTextViewText(R.id.tv_temperature_value, temperature.toString())
        val mode = modes[modeIndex]
        views.setTextViewText(R.id.knob_value_tv, mode)
        views.setImageViewResource(R.id.knob_status_icn, getACModeIcon(mode)) // You may need to update this based on the mode

        // Set the Button state
        views.setTextViewText(R.id.btnPower, if (isPowerOn) "On" else "Off")
        views.setInt(
            R.id.btnPower,
            "setBackgroundResource",
            if (isPowerOn) R.drawable.button_background_enabled else R.drawable.button_background_disabled
        )

        // Set up intent for Button
        views.setOnClickPendingIntent(R.id.rl_circular_widget, getModeTogglePendingIntent(context, appWidgetId))
        views.setOnClickPendingIntent(R.id.btnPower, getPowerTogglePendingIntent(context, appWidgetId))
	   // views.setCompoundButtonChecked(R.id.device_switch, isPowerOn)
    	//views.setOnCheckedChangeResponse(R.id.device_switch, RemoteViews.RemoteResponse.fromPendingIntent(getPowerTogglePendingIntent(context, appWidgetId)))

        appWidgetManager.updateAppWidget(appWidgetId, views)
        Log.d(TAG, "Widget updated: Power ${if (isPowerOn) "On" else "Off"}, Temp: $temperature, Mode: ${modes[modeIndex]}")
    }

    private fun getACModeIcon(mode: String): Int {
        return when(mode) {
            "Cool" -> R.drawable.ic_ac_mode_cool
            "Dry" -> R.drawable.ic_ac_mode_dry
            "Fan" -> R.drawable.ic_ac_mode_fan
            else -> R.drawable.ic_ac_mode_auto
        }
    }
}