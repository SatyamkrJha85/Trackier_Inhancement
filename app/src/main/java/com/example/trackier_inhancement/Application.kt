package com.example.trackier_inhancement

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.appsflyer.AppsFlyerLib
import com.trackier.sdk.DeepLink
import com.trackier.sdk.DeepLinkListener
import com.trackier.sdk.TrackierSDK
import com.trackier.sdk.TrackierSDKConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Application:Application() {


    object deepLinkListener : DeepLinkListener {
        override fun onDeepLinking(result: DeepLink) {
            // we have deepLink object and we can get any valve from Object
            Log.d("DeepLinkHandlerData", "deeplink getDeepLinkValue " + result.getDeepLinkValue())
            Log.d("DeepLinkHandlerData", "deeplink getStringValue " + result.getStringValue("deep_link"))
            Log.d("DeepLinkHandlerData", "deeplink getData " + result.getData())
            Log.d("DeepLinkHandlerData", "deeplink getUrl " + result.getUrl())
            Log.d("DeepLinkHandlerData", "deeplink getCampaign " + result.getCampaign())
            Log.d("DeepLinkHandlerData", "deeplink getP1 " + result.getP1())
            Log.d("DeepLinkHandlerData", "deeplink getP2 " + result.getP2())

        }
    }

    override fun onCreate() {
        super.onCreate()

        val sdkConfig = TrackierSDKConfig(this, "be82576a-b4e8-40a7-8fe2-c25d924ddc58", "development")
        sdkConfig.setDeepLinkListener(deepLinkListener)
        TrackierSDK.initialize(sdkConfig,this)


        TrackierSDK.enableSensorTracking()
        sdkConfig.getMinSessionDuration()

        TrackierSDK.enableSensorTracking()

        // Example usage in your application
        val fcmToken = "T*932H323rjjjDE#Hkfsf_TOken"
        TrackierSDK.sendFcmToken(fcmToken)


      //   Start the session tracking in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            TrackierSDK.trackSession()
        }
        println(sdkConfig.toString())


        // Aps flyer
        AppsFlyerLib.getInstance().init("pM8cmRzy2KQ5aFA4N4aoNN", null, this)
        AppsFlyerLib.getInstance().start(this)


        // Adjust


        val appToken = "cFaqWupQV4BTCgpXFkQY1jgiAFggSdPl"
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION // Use ENVIRONMENT_PRODUCTION for production

        val config = AdjustConfig(this, appToken, environment)
        config.setLogLevel(LogLevel.VERBOSE) // Optional: set log level

        Adjust.onCreate(config)



    }


   // val sdkConfig = TrackierSDKConfig(this, "cca7ae7e-239f-48e5-9990-864403e46686", "development")






}