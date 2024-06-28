package com.example.acwidget

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var tvPowerStatus: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvMode: TextView
    private lateinit var btnDecreaseTemp: Button
    private lateinit var btnIncreaseTemp: Button
    private lateinit var btnChangeMode: Button
    private lateinit var btnTogglePower: Button

    private val modes = listOf("Auto", "Cool", "Dry", "Fan")
    private lateinit var prefs: ACWidgetPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        prefs = ACWidgetPreference(this)
        initializeViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        updateAllUI()
    }

    private fun initializeViews() {
        tvPowerStatus = findViewById(R.id.tvPowerStatus)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvMode = findViewById(R.id.tvMode)
        btnDecreaseTemp = findViewById(R.id.btnDecreaseTemp)
        btnIncreaseTemp = findViewById(R.id.btnIncreaseTemp)
        btnChangeMode = findViewById(R.id.btnChangeMode)
        btnTogglePower = findViewById(R.id.btnTogglePower)
    }

    private fun setupListeners() {
        btnTogglePower.setOnClickListener {
            Log.d(TAG, "Toggle power button clicked")
            prefs.isPowerOn = !prefs.isPowerOn
            updatePowerUI()
            updateWidgets()
        }

        btnDecreaseTemp.setOnClickListener {
            Log.d(TAG, "Decrease temperature button clicked")
            if (prefs.temperature > 16) {
                prefs.temperature--
                updateTemperatureUI()
                updateWidgets()
            }
        }

        btnIncreaseTemp.setOnClickListener {
            Log.d(TAG, "Increase temperature button clicked")
            if (prefs.temperature < 30) {
                prefs.temperature++
                updateTemperatureUI()
                updateWidgets()
            }
        }

        btnChangeMode.setOnClickListener {
            Log.d(TAG, "Change mode button clicked")
            prefs.modeIndex = (prefs.modeIndex + 1) % modes.size
            updateModeUI()
            updateWidgets()
        }
    }

    private fun updateAllUI() {
        Log.d(TAG, "Updating all UI")
        updatePowerUI()
        updateTemperatureUI()
        updateModeUI()
    }

    private fun updatePowerUI() {
        Log.d(TAG, "Updating power UI")
        tvPowerStatus.text = if (prefs.isPowerOn) "Power: ON" else "Power: OFF"
        btnTogglePower.text = if (prefs.isPowerOn) "Turn Off" else "Turn On"
    }

    private fun updateTemperatureUI() {
        Log.d(TAG, "Updating temperature UI")
        tvTemperature.text = "Temperature: ${prefs.temperature}°C"
    }

    private fun updateModeUI() {
        Log.d(TAG, "Updating mode UI")
        tvMode.text = "Mode: ${modes[prefs.modeIndex]}"
    }

    private fun updateWidgets() {
        Log.d(TAG, "Sending UPDATE_WIDGET_ACTION broadcast")
        val intent = Intent(this, ACWidget::class.java).apply {
            action = UPDATE_WIDGET_ACTION
        }
        sendBroadcast(intent)
    }
}