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

            // Button to Create Short Links
            Button(
                onClick = {
                    coroutineScope.launch {

                        AppsFlyerLib.getInstance().setAppInviteOneLink("dRuK")
                        val linkGenerator =
                            ShareInviteHelper.generateInviteUrl(context)
                        linkGenerator.setCampaign("HelloCampaign")

                        linkGenerator.addParameter("af_custom_shortlink", "SendSortLink")


                        val logInviteMap = HashMap<String, String>().apply {
                            put("referrerId", "REFERRER_ID") // Replace REFERRER_ID with the actual value
                            put("campaign", "summer_sale")
                        }

                        ShareInviteHelper.logInvite(context, "mobile_share", logInviteMap)
                        Log.d("AppsflyerInvideLink",logInviteMap.toString())


                        val listener = object : LinkGenerator.ResponseListener {
                            override fun onResponse(s: String) {
                                Log.d("ShareInviteLink", "Share invite link: $s")
                                // ...
                            }

                            override fun onResponseError(s: String) {
                                Log.d("Fail Invite Link", "onResponseError called")
                            }
                        }
                        linkGenerator.generateLink(context, listener)

                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Create Short Link")
            }


            shareInviteLink(context,"HelloLink")

            CreateAndShareInviteLinkButton()
            CreateAppsFlyerShortLinkButton()


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



@Composable
fun CreateShortLinkButton() {
    val context = LocalContext.current // Get the current context
    val coroutineScope = rememberCoroutineScope() // Remember the coroutine scope

    Button(
        onClick = {
            coroutineScope.launch {
                try {
                    // Configure AppsFlyer OneLink
                    AppsFlyerLib.getInstance().setAppInviteOneLink("dRuK")

                    // Generate the invite URL
                    val linkGenerator = ShareInviteHelper.generateInviteUrl(context).apply {
                        setCampaign("HelloCampaign")
                        addParameter("af_custom_shortlink", "SendSortLink")
                    }

                    // Log the invite details
                    val logInviteMap = mapOf(
                        "referrerId" to "REFERRER_ID", // Replace with actual referrer ID
                        "campaign" to "summer_sale"
                    )
                    ShareInviteHelper.logInvite(context, "mobile_share", logInviteMap)
                    Log.d("AppsflyerInviteLink", logInviteMap.toString())

                    // Response listener for generated link
                    val listener = object : LinkGenerator.ResponseListener {
                        override fun onResponse(s: String) {
                            Log.d("ShareInviteLink", "Generated invite link: $s")
                            // Handle success (e.g., display or share the link)
                        }

                        override fun onResponseError(s: String) {
                            Log.e("FailInviteLink", "Error generating link: $s")
                            // Handle failure (e.g., show error message)
                        }
                    }

                    // Generate the link with the listener
                    linkGenerator.generateLink(context, listener)
                } catch (e: Exception) {
                    Log.e("AppsFlyerError", "Error creating short link: ${e.message}")
                    // Handle exceptions (e.g., display a toast or log error)
                }
            }
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Create Short Link")
    }
}


@Composable
fun CreateAndShareInviteLinkButton() {
    val context = LocalContext.current // Obtain the current context
    val coroutineScope = rememberCoroutineScope() // Remember coroutine scope

    Button(
        onClick = {
            coroutineScope.launch {
                try {
                    // Set up AppsFlyer OneLink
                    AppsFlyerLib.getInstance().setAppInviteOneLink("dRuK")

                    // Generate the invite URL
                    val linkGenerator = ShareInviteHelper.generateInviteUrl(context).apply {
                        setCampaign("HelloCampaign")
                        addParameter("af_custom_shortlink", "SendSortLink")
                        addParameter("referrerId", "REFERRER_ID") // Replace with actual referrer ID
                        addParameter("campaign", "summer_sale")
                    }

                    // Listener to handle the response
                    val listener = object : LinkGenerator.ResponseListener {
                        override fun onResponse(s: String) {
                            Log.d("ShareInviteLink", "Generated invite link: $s")
                            shareInviteLink(context, s) // Share the link
                        }

                        override fun onResponseError(s: String) {
                            Log.e("FailInviteLink", "Error generating invite link: $s")
                        }
                    }

                    // Generate the link
                    linkGenerator.generateLink(context, listener)

                    // Log the invite details
                    val logInviteMap = mapOf(
                        "referrerId" to "REFERRER_ID",
                        "campaign" to "summer_sale"
                    )
                    ShareInviteHelper.logInvite(context, "mobile_share", logInviteMap)
                    Log.d("AppsflyerInviteLink", logInviteMap.toString())

                } catch (e: Exception) {
                    Log.e("AppsFlyerError", "Error creating invite link: ${e.message}")
                }
            }
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Create and Share Invite Link")
    }
}

// Function to share the invite link
private fun shareInviteLink(context: Context, link: String) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check this out: $link") // Customize message
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Invite Link"))
    } catch (e: Exception) {
        Log.e("ShareInviteError", "Error sharing invite link: ${e.message}")
    }
}


@Composable
fun CreateAppsFlyerShortLinkButton() {
    val context = LocalContext.current // Get the current context
    val coroutineScope = rememberCoroutineScope() // Remember the coroutine scope

    Button(
        onClick = {
            coroutineScope.launch {
                try {
                    // Configure AppsFlyer OneLink
                    AppsFlyerLib.getInstance().setAppInviteOneLink("dRuK")

                    // Generate the invite URL
                    val linkGenerator = ShareInviteHelper.generateInviteUrl(context).apply {
                        setCampaign("HelloCampaign")
                        addParameter("af_custom_shortlink", "SendSortLink")
                    }

                    // Log the invite details
                    val logInviteMap = mapOf(
                        "referrerId" to "REFERRER_ID", // Replace with actual referrer ID
                        "campaign" to "summer_sale"
                    )
                    ShareInviteHelper.logInvite(context, "mobile_share", logInviteMap)
                    Log.d("AppsflyerInviteLink", logInviteMap.toString())

                    // Response listener for generated link
                    val listener = object : LinkGenerator.ResponseListener {
                        override fun onResponse(s: String) {
                            Log.d("ShareInviteLink", "Generated invite link: $s")
                            // Handle success (e.g., display or share the link)
                        }

                        override fun onResponseError(s: String) {
                            Log.e("FailInviteLink", "Error generating link: $s")
                            // Handle failure (e.g., show error message)
                        }
                    }

                    // Generate the link with the listener
                    linkGenerator.generateLink(context, listener)
                } catch (e: Exception) {
                    Log.e("AppsFlyerError", "Error creating short link: ${e.message}")
                    // Handle exceptions (e.g., display a toast or log error)
                }
            }
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Create Appsflyer Short Link")
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