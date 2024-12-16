package com.example.trackier_inhancement

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.trackier_inhancement.ui.theme.Trackier_InhancementTheme
import com.trackier.sdk.TrackierSDK
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SensorManager and accelerometer
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            Trackier_InhancementTheme {
                MainScreen(sensorManager, accelerometer)
            }
        }
    }
}

@Composable
fun MainScreen(sensorManager: SensorManager, accelerometer: Sensor?) {

    val coroutineScope = rememberCoroutineScope()
    var shareUrl by remember { mutableStateOf("") }



    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {


            // Button for Sending Events
            Button(
                onClick = {
                    val event = com.trackier.sdk.TrackierEvent(com.trackier.sdk.TrackierEvent.UPDATE)
                    event.param1 = "Param_Name"
                    event.couponCode = "TEST_COUPON"
                    event.discount = 10.5f
                    TrackierSDK.trackEvent(event)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Track Event")
            }

            // Button to Create Short Links
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val response = TrackierSDK.createDynamicLink("https://example.com/product") {
                                setTitle("Amazing Product")
                                setDescription("Check out this amazing product")
                                setImageUrl("https://example.com/product.jpg")
                                setCustomPath("amazing-product")
                                setCampaign("summer-sale")
                            }
                            Log.d("ShortLink", response.shortUrl)
                            shareUrl = response.shortUrl
                        } catch (e: Exception) {
                            Log.e("ShortLinkError", e.message ?: "Error creating short link")
                        }
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Create Short Link")
            }

            // Share URL Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = shareUrl,
                    onValueChange = { shareUrl = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .padding(8.dp)
                )
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Join me on this app! $shareUrl")
                                type = "text/plain"
                            }
                          //  it.context.startActivity(Intent.createChooser(sendIntent, null))
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

