package com.example.trackier_library.dynamic_link
import android.net.Uri

class DynamicLink private constructor() {
    private var templateId: String = ""
    private var link: Uri? = null
    private var domainUriPrefix: String = ""
    private var campaign: String = ""
    private var channel: String = ""
    private var mediaSource: String = ""
    private var deepLinkValue: String = ""
    private var androidParameters: AndroidParameters? = null
    private var iosParameters: IosParameters? = null
    private var desktopParameters: DesktopParameters? = null
    private var sdkParameters: Map<String, String> = emptyMap()
    private var attributionParameters: Map<String, String> = emptyMap()
    private var socialMetaTagParameters: SocialMetaTagParameters? = null

    class Builder {
        private val dynamicLink = DynamicLink()

        fun setTemplateId(templateId: String) = apply { dynamicLink.templateId = templateId }
        fun setLink(link: Uri) = apply { dynamicLink.link = link }
        fun setDomainUriPrefix(domainUriPrefix: String) = apply { dynamicLink.domainUriPrefix = domainUriPrefix }
        fun setCampaign(campaign: String) = apply { dynamicLink.campaign = campaign }
        fun setChannel(channel: String) = apply { dynamicLink.channel = channel }
        fun setMediaSource(mediaSource: String) = apply { dynamicLink.mediaSource = mediaSource }
        fun setDeepLinkValue(deepLinkValue: String) = apply { dynamicLink.deepLinkValue = deepLinkValue }
        fun setAndroidParameters(androidParameters: AndroidParameters) = apply { dynamicLink.androidParameters = androidParameters }
        fun setIosParameters(iosParameters: IosParameters) = apply { dynamicLink.iosParameters = iosParameters }
        fun setDesktopParameters(desktopParameters: DesktopParameters) = apply { dynamicLink.desktopParameters = desktopParameters }
        fun setSDKParameters(sdkParameters: Map<String, String>) = apply { dynamicLink.sdkParameters = sdkParameters }
        fun setAttributionParameters(attributionParameters: Map<String, String>) = apply { dynamicLink.attributionParameters = attributionParameters }
        fun setSocialMetaTagParameters(socialMetaTagParameters: SocialMetaTagParameters) = apply { dynamicLink.socialMetaTagParameters = socialMetaTagParameters }

        fun build(): DynamicLink {
            return dynamicLink
        }
    }

    fun toDynamicLinkConfig(): DynamicLinkConfig {
        return DynamicLinkConfig(
            installId = "rfdfkj43rjwer", // Replace with actual install ID
            appKey = "9944-djfjf3-43333", // Replace with actual app key
            templateId = templateId,
            link = link.toString(),
            brandDomain = domainUriPrefix,
            deepLinkValue = deepLinkValue,
            sdkParameter = sdkParameters,
            redirection = Redirection(
                android = androidParameters?.redirectLink,
                ios = iosParameters?.redirectLink,
                desktop = desktopParameters?.redirectLink
            ),
            attrParameter = attributionParameters,
            campaign = campaign,
            mediaSource = mediaSource,
            channel = channel,
            socialMedia = socialMetaTagParameters?.let {
                SocialMedia(it.title, it.description, it.imageLink)
            }
        )
    }
}