# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#proguard rules for library
-keep public class com.trackier.sdk.TrackierSDK**{*;}
-keep public class com.trackier.sdk.TrackierSDKConfig**{*;}


#proguard  rules for intallreferrer
-keep public class com.android.installreferrer.* { *; }
-keep public class com.android.installreferrer.api.** { *; }

# proguard- rules OkHttp, Retrofit and Moshi
-dontwarn okhttp3.**
-keep class retrofit2.** { *; }
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class com.squareup.moshi.**{*;}
-keep class com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory**{*;}

-keep @com.squareup.moshi.JsonQualifier interface *

# Kotlin and kotlinx proguard rules
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.reflect.jvm.internal.**
-keep class kotlin.Metadata { *; }
-keep class kotlin.** { *; }
-dontwarn kotlin.**
-keep class kotlinx.** {  volatile <fields>; }
-dontwarn kotlinx.**
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keep class com.google.android.material.** { *; }

-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }


# More rule

# Keep LinkedHashMap (prevent ProGuard from stripping it)
-keep class java.util.LinkedHashMap { *; }

# Keep all Map-related classes to ensure compatibility with Moshi
-keep class java.util.Map { *; }

# Keep all Moshi adapters and any generated code (to support reflection-based serialization)
-keep class com.squareup.moshi.JsonAdapter { *; }
-keepclassmembers class * {
    @com.squareup.moshi.JsonAdapter <fields>;
}

# Keep all classes used by Moshi
-keep class com.squareup.moshi.** { *; }
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# Keep LinkedHashMap and Map
-keep class java.util.LinkedHashMap { *; }
-keep class java.util.Map { *; }

# Keep all classes that are serialized using Moshi (avoid issues with missing adapters)
-keepclassmembers,allowobfuscation class * {
    @com.squareup.moshi.JsonAdapter <fields>;
}

# Keep Moshi classes
-keep class com.squareup.moshi.** { *; }

# Keep classes with JsonQualifier annotations
-keep @com.squareup.moshi.JsonQualifier interface *

