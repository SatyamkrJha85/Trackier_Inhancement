package com.example.trackier_inhancement

import android.app.Application
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.appsflyer.AppsFlyerLib
import com.trackier.sdk.TrackierSDK
import com.trackier.sdk.TrackierSDKConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Application:Application() {

    override fun onCreate() {
        super.onCreate()

        val sdkConfig = TrackierSDKConfig(this, "cca7ae7e-239f-48e5-9990-864403e46686", "testing")
        TrackierSDK.initialize(sdkConfig,this)


        TrackierSDK.enableSensorTracking()
        sdkConfig.getMinSessionDuration()

        TrackierSDK.enableSensorTracking()


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