package com.example.trackier_library.Dynamic_Link

import android.content.Context
import com.trackier.sdk.APIRepository
import com.trackier.sdk.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class DynamicLinkManager(private val context: Context) {

    // Initialize OkHttpClient with custom SSL configuration
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .sslSocketFactory(createSslSocketFactory(), createTrustManager()) // Use the custom SSL factory
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .hostnameVerifier { _, _ -> true } // For development only; remove or modify for production
            .build()
    }

    // Define Retrofit Service
    private val dynamicLinkService: DynamicLinkService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Replace with your API base URL
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .client(client) // Use the custom OkHttpClient instance
            .build()

        retrofit.create(DynamicLinkService::class.java)
    }

    suspend fun createDynamicLink(config: DynamicLinkConfig): DynamicLinkResponse {
        return withContext(Dispatchers.IO) {
            dynamicLinkService.createDynamicLink(config)
        }
    }

    suspend fun getDynamicLinkAnalytics(linkId: String): DynamicLinkAnalytics {
        return withContext(Dispatchers.IO) {
            dynamicLinkService.getDynamicLinkAnalytics(linkId)
        }
    }
}

// Create a TrustManager that accepts all certificates (useful for development only)
fun createTrustManager(): X509TrustManager {
    return object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // Add certificate checks for development or production
            // For development, you might not need to implement this
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // Validate server certificates against trusted CA here
            // For development, you might choose to skip or add custom logic
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            // Return an empty array to avoid NullPointerException
            return arrayOf()
        }
    }
}

// Create an SSLSocketFactory that uses the custom TrustManager
fun createSslSocketFactory(): SSLSocketFactory {
    return try {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(createTrustManager()), java.security.SecureRandom())
        sslContext.socketFactory
    } catch (e: Exception) {
        throw RuntimeException("Failed to create SSLSocketFactory", e)
    }
}

