package com.example.trackier_library.Dynamic_Link


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Model

@Keep
@JsonClass(generateAdapter = true)
data class DynamicLinkConfig(
    val longUrl: String,
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val customPath: String? = null,
    val customDomain: String? = null,
    val campaign: String? = null,
    val medium: String? = null,
    val source: String? = null,
    val additionalParams: Map<String, String>? = null
)

@Keep
@JsonClass(generateAdapter = true)
data class DynamicLinkResponse(
    @Json(name = "shortUrl") val shortUrl: String,
    @Json(name = "longUrl") val longUrl: String,
    @Json(name = "analytics") val analytics: DynamicLinkAnalytics?
)

@Keep
@JsonClass(generateAdapter = true)
data class DynamicLinkAnalytics(
    @Json(name = "clicks") val clicks: Int,
    @Json(name = "uniqueClicks") val uniqueClicks: Int
)



class DynamicLinkBuilder {
    private var title: String? = null
    private var description: String? = null
    private var imageUrl: String? = null
    private var customPath: String? = null
    private var customDomain: String? = null
    private var campaign: String? = null
    private var medium: String? = null
    private var source: String? = null
    private val additionalParams = mutableMapOf<String, String>()

    fun setTitle(title: String) = apply { this.title = title }
    fun setDescription(description: String) = apply { this.description = description }
    fun setImageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
    fun setCustomPath(customPath: String) = apply { this.customPath = customPath }
    fun setCustomDomain(customDomain: String) = apply { this.customDomain = customDomain }
    fun setCampaign(campaign: String) = apply { this.campaign = campaign }
    fun setMedium(medium: String) = apply { this.medium = medium }
    fun setSource(source: String) = apply { this.source = source }
    fun addParameter(key: String, value: String) = apply { additionalParams[key] = value }

    fun build(longUrl: String): DynamicLinkConfig {
        return DynamicLinkConfig(
            longUrl, title, description, imageUrl, customPath, customDomain,
            campaign, medium, source, additionalParams
        )
    }
}
