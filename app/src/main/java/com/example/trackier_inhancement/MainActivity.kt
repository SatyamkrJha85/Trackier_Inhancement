package com.example.trackier_inhancement

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var webSocketClient: WebSocketClient

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

        // Replace "your-user-id" with the actual user ID
        webSocketClient = WebSocketClient("your-user-id")
        webSocketClient.connect()
    }


}

@Composable
fun MainScreen(sensorManager: SensorManager, accelerometer: Sensor?) {

    val coroutineScope = rememberCoroutineScope()
    var shareUrl by remember { mutableStateOf("") }
    val context = LocalContext.current // Get the current context



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
                    createDynamicLink()
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Track Event")
            }

            // Button to Create Short Links
            Button(
                onClick = {
                    coroutineScope.launch {




//                        try {
//                            val response = TrackierSDK.createDynamicLink("https://example.com/product") {
//                                setTitle("Amazing Product")
//                                setDescription("Check out this amazing product")
//                                setImageUrl("https://example.com/product.jpg")
//                                setCustomPath("amazing-product")
//                                setCampaign("summer-sale")
//                            }
//                            Log.d("ShortLink", response.shortUrl)
//                            shareUrl = response.shortUrl
//                        } catch (e: Exception) {
//                            Log.e("ShortLinkError", e.message ?: "Error creating short link")
//                        }

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

                // Cloudflare point
                // Server Deployment // on Dashboard instance
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

private fun createDynamicLink() {

    // Build the dynamic link parameters
    val dynamicLink = DynamicLink.Builder()
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
                .setTitle("Your Title")
                .setDescription("Your Description")
                .setImageLink("https://www.example.com/image.jpg")
                .build()
        )
        .build()
    // Call the SDK to create the dynamic link
    TrackierSDK.createDynamicLink(dynamicLink,
        onSuccess = { dynamicLinkUrl ->
            // Use the generated link
            Log.d("dynamicsucess", dynamicLink.toString())
            println("Dynamic Link Created: $dynamicLinkUrl")
            Log.d("Dynamic Link Result",dynamicLinkUrl)
        },
        onFailure = { errorMessage ->
//            // Handle the errors
//            Log.d("dynamicfailedtoken","the token is  ${TrackierSDK.getAppToken()}")
          Log.d("dynamicfailedtoken","the Install id  is  ${TrackierSDK.getTrackierId()}")

//            Log.d("dynamicfailed", dynamicLink.toString())
            println("Failed to create dynamic link: $errorMessage")
             Log.d("dynamicfaild", errorMessage)

        }
    )
}