package com.example.trackier_inhancement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.trackier_inhancement.ui.theme.Trackier_InhancementTheme
import com.trackier.sdk.TrackierEvent
import com.trackier.sdk.TrackierSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Trackier_InhancementTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val coroutine = rememberCoroutineScope()

                    Column (
                        modifier = Modifier.padding(innerPadding)
                    ){
                        Button(onClick = {
                            val event = TrackierEvent(TrackierEvent.UPDATE)
                            event.param1 = "Param_Name"
                            event.couponCode = "TEST_COUPON"
                            event.discount = 10.5f
//            event.c_code = "testing"
//            event.discount = 3f
//            TrackierSDK.setUserName("abcd")
//            TrackierSDK.setUserPhone("1234456545")
                            TrackierSDK.trackEvent(event)
                            Log.d("TAG", "onClick: event_track ")
                        }) {
                            Text("Send Now")

                        }

                        Button(onClick = {

                            coroutine.launch {


                                val response = TrackierSDK.createDynamicLink("https://example.com/product") {
                                    setTitle("Amazing Product")
                                    setDescription("Check out this amazing product")
                                    setImageUrl("https://example.com/product.jpg")
                                    setCustomPath("amazing-product")
                                    setCampaign("summer-sale")
                                }
                                println("Short URL: ${response.shortUrl}")

                                Log.d("ShortlInkk","${response.shortUrl}")

                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Join me on this app! ${response.shortUrl}")
                                    type = "text/plain"
                                }
                                startActivity(Intent.createChooser(sendIntent, null))

                            }

                        }) {
                            Text("Short_Link")
                        }

                        Button(onClick = {

                            val deepLink = "https://yourapp.app.goo.gl/your_referral_code"

                        }) {

                            Text("Send Invite")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
 val event = TrackierEvent.INVITE
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Trackier_InhancementTheme {
        Greeting("Android")
    }
}