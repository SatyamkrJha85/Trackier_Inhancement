# Suppress warnings from external dependencies
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.google.gson.**
-dontwarn com.trackier.**


# Keep only necessary Trackier SDK classes
-keep class com.trackier.sdk.** { *; }

# Keep Google Play Services classes for Ads & Install Referrer
-keep class com.google.android.gms.common.ConnectionResult { int SUCCESS; }
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}

# Keep Install Referrer API
-keep public class com.android.installreferrer.** { *; }

#
-dontwarn kotlin.reflect.jvm.**
-dontwarn kotlin.reflect.full.**
-dontwarn kotlin.reflect.jvm.KCallablesJvm
-dontwarn kotlin.reflect.full.KClasses
-dontwarn kotlin.reflect.jvm.internal.**

  -keep class kotlin.Metadata { *; }
  -keep class kotlin.reflect.jvm.internal.** { *; }
#  -keep class kotlin.** { *; } #only this is remaining to comment
  -dontwarn kotlin.**

-keep class kotlin.** {
    public protected *;
}


  # Optimize App Size - Remove unused code & logs
  #-assumenosideeffects class android.util.Log { *; }