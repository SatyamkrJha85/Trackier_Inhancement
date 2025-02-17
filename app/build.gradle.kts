plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.trackier_inhancement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.trackier_inhancement"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "12345678"
            keyAlias = "my-key-alias"
            keyPassword = "12345678"
        }
    }

    buildTypes {
        release {

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)


    // before 4 mb app size after adding excule now size is 3.9 and after add proguard now size is 3.4

    // after adding this : -assumenosideeffects class android.util.Log { *; } now size is 3.1

    implementation(project(":Trackier_Library")){
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect") // Exclude reflection
    }

    implementation("com.squareup.moshi:moshi:1.14.0") // Core Moshi library
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0") // Kotlin Moshi without reflection
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0") // Codegen-based Moshi (No reflection)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.appsflyer:af-android-sdk:6.12.1")
    implementation ("com.android.installreferrer:installreferrer:2.2")
    implementation ("com.adjust.sdk:adjust-android:4.33.0")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")


}