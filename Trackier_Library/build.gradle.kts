import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    alias(libs.plugins.android.library)
    id("kotlin-kapt")
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" // Use a valid version

}
val kotlin_version = "2.1.0" // Replace with the Kotlin version you want to use

android {
    namespace = "com.example.trackier_library"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        multiDexEnabled = true


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }



    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

    // Annotations
    implementation("androidx.annotation:annotation:1.4.0")

    // Coroutines dependancy change to 1.6.0 to 1.10.1
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    // WorkManager
    val work_version = "2.7.1"
    implementation("androidx.work:work-runtime-ktx:$work_version")

    // JSON Parsing
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")

    // Referrer APIs
    implementation("com.android.installreferrer:installreferrer:2.2")
    // remove this dependancy
    //implementation("com.miui.referrer:homereferrer:1.0.0.6")
//    val multidex_version = "2.0.1"
//    implementation ("androidx.multidex:multidex:$multidex_version")
    // Networking
    val retrofit2_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit2_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit2_version")

    // Oaid

    implementation ("com.huawei.hms:ads-identifier:3.4.56.300")
}