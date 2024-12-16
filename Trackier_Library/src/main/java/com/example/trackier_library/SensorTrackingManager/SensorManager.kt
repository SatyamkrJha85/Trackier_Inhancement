package com.trackier.sdk.SensorTrackingManager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.concurrent.ConcurrentHashMap

class SensorTrackingManager(private val context: Context) {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorsToTrack = listOf(
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_GYROSCOPE,
        Sensor.TYPE_MAGNETIC_FIELD,       // Magnetic Field for compass-like data
        Sensor.TYPE_LIGHT,               // Light sensor for ambient light measurement
        Sensor.TYPE_PROXIMITY            // Proximity sensor for detecting nearby objects
    )

    private val sensorData = ConcurrentHashMap<String, Float>() // Thread-safe map for sensor data
    private val activeSensors = mutableListOf<Sensor>()

    fun startTracking() {
        sensorsToTrack.forEach { sensorType ->
            val sensor = sensorManager.getDefaultSensor(sensorType)
            if (sensor != null) {
                activeSensors.add(sensor)
                sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    fun stopTracking() {
        activeSensors.forEach { sensor ->
            sensorManager.unregisterListener(sensorEventListener, sensor)
        }
        activeSensors.clear()
    }

    fun getSensorData(): Map<String, Float> = sensorData

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val sensorValues = it.values
                when (it.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        sensorData["accelerometer_x"] = sensorValues[0]
                        sensorData["accelerometer_y"] = sensorValues[1]
                        sensorData["accelerometer_z"] = sensorValues[2]
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        sensorData["gyroscope_x"] = sensorValues[0]
                        sensorData["gyroscope_y"] = sensorValues[1]
                        sensorData["gyroscope_z"] = sensorValues[2]
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        sensorData["magnetic_field_x"] = sensorValues[0]
                        sensorData["magnetic_field_y"] = sensorValues[1]
                        sensorData["magnetic_field_z"] = sensorValues[2]
                    }
                    Sensor.TYPE_LIGHT -> {
                        sensorData["light"] = sensorValues[0]
                    }
                    Sensor.TYPE_PROXIMITY -> {
                        sensorData["proximity"] = sensorValues[0]
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Optional: Log or handle sensor accuracy changes
        }
    }
}
