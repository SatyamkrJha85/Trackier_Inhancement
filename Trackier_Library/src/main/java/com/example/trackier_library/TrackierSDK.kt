package com.trackier.sdk

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.Keep
import androidx.work.Logger
import com.example.trackier_library.dynamic_link.DynamicLink
import com.huawei.hms.ads.identifier.AdvertisingIdClient
import com.trackier.sdk.SensorTrackingManager.SensorTrackingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.Date

@Keep
object TrackierSDK {
    private var isInitialized = false
    private val logger = Factory.logger
    private var instance = TrackierSDKInstance()
    private lateinit var sensorTrackingManager: SensorTrackingManager
    private var isSensorTrackingEnabled = false

    @JvmStatic
    fun initialize(config: TrackierSDKConfig, application: Application) {
        sensorTrackingManager = SensorTrackingManager(application)

        if (isInitialized) {
            logger.finest("SDK Already initialized")
            return
        }
        isInitialized = true
        logger.info("Trackier SDK ${Constants.SDK_VERSION} initialized")
        instance.initialize(config)

    }

    @JvmStatic
    fun getAppToken(): String {
        return instance.getAppToken()
    }


    @JvmStatic
    fun isEnabled(): Boolean {
        return instance.isEnabled
    }

    @JvmStatic
    fun setEnabled(value: Boolean) {
        instance.isEnabled = value
    }

    @JvmStatic
    fun trackEvent(event: TrackierEvent) {
        if (!isInitialized) {
            logger.finest("SDK Not Initialized")
            return
        }
        if (!isEnabled()) {
            logger.finest("SDK Disabled")
            return
        }
        instance.trackEvent(event)
    }

    @JvmStatic
    suspend fun trackSession() {
        instance.trackSession()
    }

    private fun logSessionBody(workRequest: TrackierWorkRequest) {
        val sessionBody = workRequest.getData()
        logger.info("Session body is: $sessionBody")
    }


    fun enableSensorTracking() {
        isSensorTrackingEnabled = true
        sensorTrackingManager.startTracking()
    }

    fun disableSensorTracking() {
        isSensorTrackingEnabled = false
        sensorTrackingManager.stopTracking()
    }

    fun isSensorTrackingEnabled(): Boolean {
        return isSensorTrackingEnabled
    }

    @JvmStatic
    fun parseDeepLink(uri: Uri?) {
        if (uri == null) return
        try {
            instance.parseDeepLink(uri)
        } catch (e: Exception) {
            Log.d("trackiersdk", "parseDeeplink " + e.message)
        }

    }

    @JvmStatic
    fun setLocalRefTrack(value: Boolean, delimeter: String = "_") {
        if (value) {
            instance.isLocalRefEnabled = value
            instance.localRefDelimeter = delimeter
        }
    }

    @JvmStatic
    fun fireInstall() {
        instance.fireInstall()
    }

    @JvmStatic
    fun setUserId(userId: String) {
        instance.customerId = userId
    }

    @JvmStatic
    fun setUserEmail(userEmail: String) {
        instance.customerEmail = userEmail
    }

    @JvmStatic
    fun setUserAdditionalDetails(userAdditionalDetails: MutableMap<String, Any>) {
        instance.customerOptionals = userAdditionalDetails
    }

    @JvmStatic
    fun getTrackierId(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_INSTALL_ID)
    }

    @JvmStatic
    fun trackAsOrganic(organic: Boolean) {
        instance.organic = organic
    }

    @JvmStatic
    fun setUserName(userName: String) {
        instance.customerName = userName
    }

    @JvmStatic
    fun setUserPhone(userPhone: String) {
        instance.customerPhoneNumber = userPhone
    }

    @JvmStatic
    fun setIMEI(imei1: String, imei2: String) {
        instance.imei1 = imei1
        instance.imei2 = imei2
    }

    @JvmStatic
    fun setMacAddress(macAddress: String) {
        instance.mac = macAddress
    }

    @JvmStatic
    fun getAd(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_AD)
    }

    @JvmStatic
    fun getAdID(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_ADID)
    }

    @JvmStatic
    fun getAdSet(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_ADSET)
    }

    @JvmStatic
    fun getAdSetID(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_ADSETID)
    }

    @JvmStatic
    fun getCampaign(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_CAMPAIGN)
    }

    @JvmStatic
    fun getCampaignID(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_CAMPAIGNID)
    }

    @JvmStatic
    fun getChannel(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_CHANNEL)
    }

    @JvmStatic
    fun getP1(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_P1)
    }

    @JvmStatic
    fun getP2(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_P2)
    }

    @JvmStatic
    fun getP3(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_P3)
    }

    @JvmStatic
    fun getP4(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_P4)
    }

    @JvmStatic
    fun getP5(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_P5)
    }

    @JvmStatic
    fun getClickId(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_CLICKID)
    }

    @JvmStatic
    fun getDlv(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_DLV)
    }

    @JvmStatic
    fun getPid(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_PID)
    }

    @JvmStatic
    fun getPartner(): String {
        return Util.getSharedPrefString(instance.config.context, Constants.SHARED_PREF_PARTNER)
    }

    @JvmStatic
    fun getIsRetargeting(): String {
        return Util.getSharedPrefString(
            instance.config.context,
            Constants.SHARED_PREF_ISRETARGETING
        )
    }

    @JvmStatic
    fun setPreinstallAttribution(pid: String, campaign: String, campaignId: String) {
        val prefs = Util.getSharedPref(instance.config.context)
        prefs.edit().putString(Constants.PRE_INSTALL_ATTRIBUTION_PID, pid)
            .putString(Constants.PRE_INSTALL_ATTRIBUTION_CAMPAIGN, campaign)
            .putString(Constants.PRE_INSTALL_ATTRIBUTION_CAMPAIGNID, campaignId)
            .apply()
    }

    enum class Gender {
        Male,
        Female,
        Others
    }

    @JvmStatic
    fun setGender(gender: Gender) {
        instance.gender = gender.toString()
    }

    @JvmStatic
    fun setDOB(dob: String) {
        instance.dob = dob
    }

    @JvmStatic
    fun storeRetargetting(context: Context, uri: String) {
        val ctx = context.applicationContext
        Util.setSharedPrefString(ctx, Constants.STORE_RETARGETING, uri)
        Util.setSharedPrefString(
            ctx, Constants.STORE_RETARGETING_TIME, Util.dateFormatter.format(
                Date()
            )
        )
    }



    @JvmStatic
    fun createDynamicLink(
        dynamicLink: DynamicLink,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = instance.createDynamicLink(dynamicLink)
            if (response.success) {
                response.data?.link?.let { link ->
                    Log.e("TrackierSDK","Your Dynamic Link is : "+ link)
                    onSuccess(link)
                } ?: onFailure("Failed to retrieve link")
            } else {
                val errorMessage = response.error?.let {
                    "Error ${it.statusCode} (${it.errorCode}): ${it.codeMsg} - ${it.message}"
                } ?: response.message ?: "Unknown error"

                Log.e("TrackierSDK", errorMessage)
                onFailure(errorMessage)
            }
        }
    }

    @JvmStatic
    fun sendFcmToken(token: String) {
        if (!isInitialized) {
            logger.finest("SDK Not Initialized")
            return
        }
        if (!isEnabled()) {
            logger.finest("SDK Disabled")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            instance.sendFcmToken(token)
        }
    }

    // New OAID Functionality
//    @JvmStatic
//    fun getOAID(context: Context, onResult: (String?) -> Unit) {
//        if (!isInitialized) {
//            logger.finest("SDK Not Initialized")
//            onResult(null)
//            return
//        }
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
//                val oaid = advertisingIdInfo?.id
//                withContext(Dispatchers.Main) {
//                    onResult(oaid)
//                }
//            } catch (e: Exception) {
//                e.message?.let { Log.e("Failed to retrieve OAID: ${e.message}", it) }
//                withContext(Dispatchers.Main) {
//                    onResult(null)
//                }
//            }
//        }
//    }

//    @JvmStatic
//    fun logOAID(context: Context) {
//        getOAID(context) { oaid ->
//            if (oaid != null) {
//                logger.info("OAID: $oaid")
//                Log.d("TrackierSDK", "OAID retrieved: $oaid")
//            } else {
//                logger.warning("OAID not available")
//                Log.d("TrackierSDK", "OAID not available")
//            }
//        }
//    }

}

