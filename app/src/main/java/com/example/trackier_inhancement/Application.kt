package com.example.trackier_inhancement

import android.app.Application
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.trackier.sdk.TrackierSDK
import com.trackier.sdk.TrackierSDKConfig

class Application:Application() {

    override fun onCreate() {
        super.onCreate()

        val sdkConfig = TrackierSDKConfig(this, "cca7ae7e-239f-48e5-9990-864403e46686", "testing")
        TrackierSDK.initialize(sdkConfig)

    }


   // val sdkConfig = TrackierSDKConfig(this, "cca7ae7e-239f-48e5-9990-864403e46686", "development")






}