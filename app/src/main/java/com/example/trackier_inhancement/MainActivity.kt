package com.example.trackier_inhancement

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.MainThread
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adjust.sdk.Adjust
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerLib
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.example.trackier_inhancement.ui.theme.Trackier_InhancementTheme
import com.example.trackier_library.dynamic_link.*
import com.trackier.sdk.Constants
import com.trackier.sdk.TrackierSDK
import com.trackier.sdk.TrackierSDKInstance
import com.trackier.sdk.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Retrieve deep link data
        val deepLinkUri: Uri? = intent?.data
        deepLinkUri?.let {
            Log.d("DeepLinkHandlerData", "Received Deep Link URI: $it")
            TrackierSDK.parseDeepLink(it)
            finish()
        }

        setContent {
            Trackier_InhancementTheme {
                MainScreen()
            }
        }

        // Test the new resolveDeeplinkUrl function
        val testUrl = "https://trackier58.u9ilnk.me/d/PGJ2m4NtPd"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resolvedUrl = TrackierSDK.resolveDeeplinkUrl(testUrl)
                Log.d("DeeplinkResolverTest", "Resolved URL: $resolvedUrl")
            } catch (e: Exception) {
                Log.e("DeeplinkResolverTest", "Error resolving deeplink: ", e)
            }
        }

        // Test the new resolveDeeplinkUrl function
        val testUrl2 = "https://trackier58.u9ilnk.me/d/67cafc5938fa96aa7e7750c9"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resolvedUrl = TrackierSDK.resolveDeeplinkUrl(testUrl2)
                Log.d("DeeplinkResolverTest", "Resolved URL: $resolvedUrl")
            } catch (e: Exception) {
                Log.e("DeeplinkResolverTest", "Error resolving deeplink: ", e)
            }
        }

    }


}

@Composable
fun MainScreen() {

    val coroutineScope = rememberCoroutineScope()
    var shareUrl by remember { mutableStateOf("") }
    val context = LocalContext.current // Get the current context
//
//// Log OAID directly
//    TrackierSDK.logOAID(context)
//
//    // Get OAID and handle it manually
//    TrackierSDK.getOAID(context) { oaid ->
//        if (oaid != null) {
//            println("Retrieved OAID: $oaid")
//            Log.d("OAIDIS Result",oaid)
//        } else {
//            println("OAID not available")
//            Log.d("OAIDIS","OAID Failed")
//
//        }
//    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {


            // Button for Sending Events
            Button(
                onClick = {
                    val event = com.trackier.sdk.TrackierEvent(com.trackier.sdk.TrackierEvent.ADD_TO_CART)
                    event.param1 = "Param_Name"
                    event.couponCode = "TEST_COUPON"
                    event.discount = 10.5f
                    TrackierSDK.trackEvent(event)
                    createDynamicLink(context)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Track Event")
            }

            // Share URL Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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

                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}






private fun createDynamicLink(context: Context) {

    // Build the dynamic link parameters
    val dynamicLink = DynamicLink.Builder()
        .setTemplateId("78")
        .setTemplateId("7ytuiug")
        .setLink(Uri.parse("https://apptrove.com?utm_redirect=sdk_link"))
        .setDomainUriPrefix("vistmarket.shop")
        .setDeepLinkValue("NewMainActivity")
        .setAndroidParameters(
            AndroidParameters.Builder()
                .setRedirectLink("https://play.google.com/store/apps/details?id=com.trackier.vistmarket")
                .build()
        )
        .setSDKParameters(mapOf("param1" to "value1", "param2" to "value2"))
        .setAttributionParameters(
            channel = "my_channel",
            campaign = "my_campaign",
            mediaSource = "at_invite",
            p1 = "param1_value",
            p2 = "dfjsdfsdf",
            p3 = "sdfsdfsdf"
        )
        .setIosParameters(
            IosParameters.Builder()
                .setRedirectLink("https://www.example.com/ios")
                .build()
        )
        .setDesktopParameters(
            DesktopParameters.Builder()
                .setRedirectLink("https://www.example.com/desktop")
                .build()
        )
        .setSocialMetaTagParameters(
            SocialMetaTagParameters.Builder()
                .setTitle("New Offer Buy 1 Get 2 Free")
                .setDescription("New Deal is live now just Open Vist market and purchase any product and get 2 product free")
                .setImageLink("https://storage.googleapis.com/static.trackier.io/images/test-data/downloaded_images/bluetooth_speaker.jpg")
                .build()
        )
        .build()
    // Call the SDK to create the dynamic link
    TrackierSDK.createDynamicLink(dynamicLink,
        onSuccess = { dynamicLinkUrl ->
            // Intent for share link
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUrl)
            context.startActivity(Intent.createChooser(shareIntent, "Share Link"))
            // Use the generated link
            Log.d("dynamicsucess", dynamicLink.toString())
            Log.d("Dynamic Link Result",dynamicLinkUrl)
        },
        onFailure = { errorMessage ->

            // Toast Message for Failure

            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Link Failed", Toast.LENGTH_SHORT).show()
            }//            // Handle the errors
//            Log.d("dynamicfailedtoken","the token is  ${TrackierSDK.getAppToken()}")
          Log.d("dynamicfailedtoken","the Install id  is  ${TrackierSDK.getTrackierId()}")

//            Log.d("dynamicfailed", dynamicLink.toString())
            println("Failed to create dynamic link: $errorMessage")
             Log.d("dynamicfaild", errorMessage)

        }
    )
}