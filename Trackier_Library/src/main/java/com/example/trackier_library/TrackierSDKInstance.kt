package com.trackier.sdk

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import com.example.trackier_library.dynamic_link.DynamicLink
import com.example.trackier_library.dynamic_link.DynamicLinkResponse
import com.example.trackier_library.dynamic_link.LinkData
import com.trackier.sdk.Factory.logger
import com.trackier.sdk.SensorTrackingManager.SensorTrackingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TrackierSDKInstance {
    private val device = DeviceInfo()
    lateinit var config: TrackierSDKConfig
    private var refDetails: RefererDetails? = null
    private var refXiaomiDetails: XiaomiReferrerDetails? = null
    private var appToken: String = ""
    var isEnabled = true
    var isInitialized = false
    var configLoaded = false
    var gaid: String? = null
    var isLAT = false
    var installId = ""
    var isLocalRefEnabled = false
    var localRefDelimeter = ""
    var isManualInstall = false
    var disableOrganicTrack = false

    var customerId = ""
    var customerEmail = ""
    var customerName = ""
    var customerPhoneNumber = ""
    var imei1 = ""
    var imei2 = ""
    var mac = ""
    var customerOptionals: MutableMap<String, Any>? = null

    var firstInstallTime = ""
    var organic = false
    var gender = ""
    var dob = ""
    var preinstallData: MutableMap<String, Any>? = null

    private var sensorTrackingManager: SensorTrackingManager? = null
    private var sensorData: Map<String, Float> = emptyMap()



    /**
     * Initialize method should be called to initialize the sdk
     */
    fun initialize(config: TrackierSDKConfig) {
        if (configLoaded) {
            return
        }
        this.config = config
        this.configLoaded = true
        this.appToken = this.config.appToken
        this.installId = getInstallID()
        this.firstInstallTime = getFirstInstallTS()
        this.isManualInstall = config.getManualMode()
        this.disableOrganicTrack = config.getOrganicTracking()
        DeviceInfo.init(device, this.config.context)

        // Initialize sensor tracking manager
        sensorTrackingManager = SensorTrackingManager(this.config.context)
        sensorTrackingManager?.startTracking()


        // Collect sensor data when initializing
        collectSensorData()

        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..5) {
               val gadid =  initGaid()
                if(!"".equals(gadid)){
                    break
                }
                delay(1000 * i.toLong())
            }
            if (!isManualInstall) {
                initAttributionInfo()
                trackInstall()
            }
            trackSession()
            callDeepLinkListener()
        }
    }

    // Collect sensor data
    private fun collectSensorData() {
        // Get sensor data from SensorTrackingManager
        sensorData = sensorTrackingManager?.getSensorData() ?: emptyMap()
    }


    // Log session data along with sensor data
    private fun logSessionDataWithSensor() {
        val sessionData = mutableMapOf<String, Any>(
            "device" to device,
            "createdAt" to Date(),
            "isLAT" to isLAT,
            "referrer" to getReferrerDetails().url,
            "installId" to installId,
            "appKey" to appToken,
            "mode" to config.getManualMode(),
            "sdkt" to "android"
        )

        // Include sensor data in the log
        sessionData["sensorData"] = sensorData

        // Log the session data with sensor data
        Log.i("TrackierSDK", "Session body is: $sessionData")
    }




    private suspend fun initGaid(): String {
        for (i in 1..5) {
            val (gaid, isLat) = DeviceInfo.getGAID(this.config.context)
            this.gaid = gaid
            this.isLAT = isLat
            if (this.gaid != null) {
                break
            }
            delay(1000 * i.toLong())
        }
        return this.gaid.toString()
    }

    private suspend fun initAttributionInfo() {
        isInitialized = true
    }

    fun fireInstall() {
        CoroutineScope(Dispatchers.IO).launch {
            initAttributionInfo()
            trackInstall()
            trackSession()
        }
    }

    private fun isReferrerStored(): Boolean {
        val url = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_INSTALL_URL)
        return url.isNotBlank()
    }

    private fun getReferrerDetails(): RefererDetails {
        if (refDetails != null) {
            return refDetails!!
        }
        var url = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_INSTALL_URL)
        val clickTime = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_CLICK_TIME)
        val installTime = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_INSTALL_TIME)
        if (url.isBlank()) {
            url = RefererDetails.ORGANIC_REF
        }
        refDetails = RefererDetails(url, clickTime, installTime)
        return refDetails!!
    }

    private fun setReferrerDetails(refererDetails: RefererDetails) {
        refDetails = refererDetails
        try {
            val prefs = Util.getSharedPref(this.config.context)
            prefs.edit().putString(Constants.SHARED_PREF_INSTALL_URL, refererDetails.url)
                    .putString(Constants.SHARED_PREF_CLICK_TIME, refererDetails.clickTime)
                    .putString(Constants.SHARED_PREF_INSTALL_TIME, refererDetails.installTime)
                    .apply()
        } catch (ex: Exception) {}
    }

    private fun setXiaomiReferrerDetails(xiaomiRefererDetails: XiaomiReferrerDetails) {
        refXiaomiDetails = xiaomiRefererDetails
        try {
            val prefs = Util.getSharedPref(this.config.context)
            prefs.edit().putString(Constants.SHARED_PREF_XIAOMI_INSTALL_URL, refXiaomiDetails!!.installReferrer)
                .putInt(Constants.SHARED_PREF_XIAOMI_CLICKTIMESTAMP, refXiaomiDetails!!.referrerClickTimestampSeconds)
                .putInt(Constants.SHARED_PREF_XIAOMI_INSTALLTIMEBEGIN, refXiaomiDetails!!.installBeginTimestampSeconds)
                .apply()
        } catch (ex: Exception) {}
    }

    private fun setInstallID(installID: String) {
        Util.setSharedPrefString(this.config.context, Constants.SHARED_PREF_INSTALL_ID, installID)
    }

    private fun getInstallID(): String {
        var installId = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_INSTALL_ID)
        Log.d("TrackierInstallID","The id is "+installId)
        if(installId.isBlank()){
            installId = UUID.randomUUID().toString()
            setInstallID(installId)
        }
        return installId
    }

    private fun setFirstInstallTS(firstInstall: String) {
        Util.setSharedPrefString(this.config.context, Constants.SHARED_PREF_FIRST_INSTALL, firstInstall)
    }

    private fun getFirstInstallTS(): String {
        var firstInstallTime = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_FIRST_INSTALL)
        if (firstInstallTime.isBlank()) {
            firstInstallTime = Util.dateFormatter.format(Date())
            setFirstInstallTS(firstInstallTime)
        }
        return firstInstallTime
    }


    private fun makeWorkRequest(kind: String): TrackierWorkRequest {
        val trackierWorkRequest = TrackierWorkRequest(kind, appToken, this.config.env)
        if (this.config.getSDKType() != "android") {
            device.sdkVersion = this.config.getSDKVersion()
        } else {
           device.sdkVersion = Constants.SDK_VERSION
        }
        if (this.config.getAndroidId().isNotEmpty()) {
            device.androidId = this.config.getAndroidId()
        }
        device.imei1 = this.imei1
        device.imei2 = this.imei2
        device.mac = this.mac
        trackierWorkRequest.device = device
        trackierWorkRequest.gaid = gaid
        trackierWorkRequest.refDetails = getReferrerDetails()
        trackierWorkRequest.installID = installId
        trackierWorkRequest.customerId = this.customerId
        trackierWorkRequest.customerEmail = this.customerEmail
        trackierWorkRequest.customerOptionals = this.customerOptionals
        trackierWorkRequest.attributionParams = this.config.getAttributionParams()
        trackierWorkRequest.sdkt = this.config.getSDKType()
        trackierWorkRequest.disableOrganicTrack = disableOrganicTrack
        trackierWorkRequest.firstInstallTime = firstInstallTime
        trackierWorkRequest.organic = organic
        trackierWorkRequest.gender = gender
        trackierWorkRequest.dob = dob
        trackierWorkRequest.secretId = this.config.getAppSecretId()
        trackierWorkRequest.secretKey = this.config.getAppSecretKey()
        trackierWorkRequest.customerName = this.customerName
        trackierWorkRequest.customerPhoneNumber = this.customerPhoneNumber
        trackierWorkRequest.preinstallData = this.preinstallData
        trackierWorkRequest.storeRetargeting = getRetargetingData()

        return trackierWorkRequest
    }

    private fun isInstallTracked(): Boolean {
        return try {
            val prefs = Util.getSharedPref(this.config.context)
            prefs.getBoolean(Constants.SHARED_PREF_IS_INSTALL_TRACKED, false)
        } catch (ex: Exception) {
            false
        }
    }

    private fun setInstallTracked() {
        try {
            val prefs = Util.getSharedPref(this.config.context)
            prefs.edit().putBoolean(Constants.SHARED_PREF_IS_INSTALL_TRACKED, true).apply()
        } catch (ex: Exception) {}
    }

    private suspend fun trackInstall() {
        if (!isEnabled || !configLoaded) {
            return
        }
        if (isInstallTracked()) {
            return
        }
        try {
            if (!isReferrerStored()) {
                if (isLocalRefEnabled) {
                    val installRef = LocalInstallReferrer(this.config.context, this.localRefDelimeter)
                    val refDetails = installRef.getRefDetails()
                    this.setReferrerDetails(refDetails)
                } else {
                    val installRef = InstallReferrer(this.config.context)
                    val refDetails = installRef.getRefDetails()
                    this.setReferrerDetails(refDetails)
                   val xiaomiInstallRef = null
//                    if (xiaomiInstallRef != null) {
//                        this.setXiaomiReferrerDetails(xiaomiInstallRef)
//                    }
                }

            }
        } catch (ex: Exception) {
            Factory.logger.warning("Unable to get referrer data on install")
        }
        preinstallData = Util.getPreLoadAndPAIdata(config.context)
        val wrkRequest = makeWorkRequest(TrackierWorkRequest.KIND_INSTALL)
        try {
            TrackierWorkRequest.enqueue(wrkRequest)
            Log.e("TrackierSDK","Work request try block"+wrkRequest)
        } catch (ex: Exception) {
            APIRepository.processWork(wrkRequest)
            Log.e("TrackierSDK","Work request Failed block"+ex.toString())
        }

        setInstallTracked()
    }

    private suspend fun _trackEvent(event: TrackierEvent) {
        val wrkRequest = makeWorkRequest(TrackierWorkRequest.KIND_EVENT)
        wrkRequest.event = event

        try {
            TrackierWorkRequest.enqueue(wrkRequest)
        } catch (ex: Exception) {
            APIRepository.processWork(wrkRequest)
        }
    }

    fun trackEvent(event: TrackierEvent) {
        if (!isEnabled || !configLoaded) {
            return
        }
        if (!isInitialized) {
            Factory.logger.warning("Event Tracking request sent before SDK data was initialized")
        }
        if (!isInstallTracked()) {
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 1..5) {
                    delay(1000 * i.toLong())
                    if (isInstallTracked()) {
                        _trackEvent(event)
                        break
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _trackEvent(event)
            }
        }
    }

    private fun getLastSessionTime(): String {
        return Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_LAST_SESSION_TIME)
    }

    private fun setLastSessionTime(time: String) {
        val prefs = Util.getSharedPref(this.config.context)
        prefs.edit().putString(Constants.SHARED_PREF_LAST_SESSION_TIME, time)
                .apply()
    }

    // Get and set time for session


    private fun getLastSessionDate(): String {
        return Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_LAST_SESSION_DATE)
    }

    private fun setLastSessionDate(time: String) {
        val prefs = Util.getSharedPref(this.config.context)
        prefs.edit().putString(Constants.SHARED_PREF_LAST_SESSION_DATE, time)
            .apply()
    }


    suspend fun trackSession() {
        if (!isEnabled || !configLoaded) {
            return
        }
        if (!isInstallTracked()) {
            return
        }
        val currentTs = Date().time
        val currentTime = Util.dateFormatter.format(currentTs)
        //val currentDate = Util.dateFormatter.format(Date())
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        try {
            val lastSessionTime = getLastSessionTime()
            val lastSessionDate = getLastSessionDate()
            if (lastSessionDate != currentDate) {
                val wrkRequest = makeWorkRequest(TrackierWorkRequest.KIND_SESSION_TRACK)
                wrkRequest.sessionTime = lastSessionTime
                APIRepository.processWork(wrkRequest)
                setLastSessionTime(currentTime)
                setLastSessionDate(currentDate)
                Log.d("trackiersdk", "Stored last session date: '$lastSessionDate'")
                Log.d("trackiersdk", "Generated current date: '$currentDate'")
            } else {
                Log.d("trackiersdk","already called for today")
            }
        } catch (e: Exception) {}
    }

    suspend fun deeplinkData(url: Uri): ResponseData? {
        var deeplinRes: ResponseData? = null
        val wrkRequest = makeWorkRequest(TrackierWorkRequest.KIND_DEEPLINKS)
        wrkRequest.deeplinkUrl = url.toString()
        try {
            deeplinRes = APIRepository.processWork(wrkRequest)
        } catch (ex: Exception) {
            APIRepository.doWork(wrkRequest)
        }
        return deeplinRes
    }

    fun callDeepLinkListener() {
        val dlt = this.config.getDeepLinkListener() ?: return
        val isDeeplinkCalled = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_DEEP_LINK_CALLED)
        if (isDeeplinkCalled == "true") return

        val dlstr = Util.getSharedPrefString(this.config.context, Constants.SHARED_PREF_DEEP_LINK)
        val dlResult: DeepLink
        if (dlstr.isBlank()) {
            val ref = getReferrerDetails()
            if (!ref.isDeepLink) {
                return
            }
            dlResult = DeepLink(ref.url, true)
        } else {
            Util.delSharedPrefKey(this.config.context, Constants.SHARED_PREF_DEEP_LINK)
            dlResult = DeepLink(dlstr, false)
        }
        Util.setSharedPrefString(this.config.context, Constants.SHARED_PREF_DEEP_LINK_CALLED, "true")
        dlt.onDeepLinking(dlResult)
    }

    fun callDeepLinkListenerDynamic(dlObj: ResponseData) {
        val dlt = this.config.getDeepLinkListener() ?: return
        if (dlObj.data?.url?.isNotEmpty() == true) {
            val dlResult = DeepLink(
                dlObj.data.url,
                false,
                dlObj.data.sdkParams
            )
            dlt.onDeepLinking(dlResult)
        }
    }

    fun getRetargetingData(): MutableMap<String, Any> {
        val body = mutableMapOf<String, Any>()
        body["rtgtime"] = Util.getSharedPrefString(this.config.context, Constants.STORE_RETARGETING_TIME)
        body["url"] = Util.getSharedPrefString(this.config.context, Constants.STORE_RETARGETING)
        return body
    }

    fun parseDeepLink(uri: Uri?) {
        if (uri == null) return
        var resData: ResponseData? = null
        CoroutineScope(Dispatchers.Main).launch {
            try {
                resData = deeplinkData(uri)
            } catch (e: Exception) { }

            if (isInitialized) {
                try {
                    if (resData != null) {
                        resData?.let { callDeepLinkListenerDynamic(it) }
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


    // use the APIRepository to send the dynamic link data to the server.
    suspend fun createDynamicLink(dynamicLink: DynamicLink): DynamicLinkResponse {
        val configMap = dynamicLink.toDynamicLinkConfig()
        return withContext(Dispatchers.IO) {
            try {
                APIRepository.sendDynamiclinks(configMap.toMutableMap())
            } catch (e: Exception) {
                // Log the error and return a failure response
                Factory.logger.severe("Error creating dynamic link: ${e.message}")
                DynamicLinkResponse(success = false, message = "Failed to create link ${e.message}", data = LinkData(link = ""))
            }
        }
    }

    fun getAppToken(): String {
        return appToken
    }

    // Call the APi Repo and send the token to server

    suspend fun sendFcmToken(token: String): ResponseData? {
        if (!isEnabled || !configLoaded) {
            logger.warning("SDK is not enabled or config is not loaded")
            return null
        }

        // Prepare the request body
        val body = mutableMapOf<String, Any>()
        body["fcmToken"] = token

        return try {
            logger.info("Sending FCM token: $token")
            APIRepository.sendToken(body) // Call the existing sendToken method
        } catch (ex: Exception) {
            logger.severe("Failed to send FCM token: ${ex.message}")
            null
        }
    }

}