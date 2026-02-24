

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"

}



android {
    namespace = "com.example.gocab"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gocab"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(libs.androidx.animation)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.hilt.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    implementation("androidx.core:core-ktx:1.13.1")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
//    implementation("androidx.activity:activity-compose:1.10.1")

    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")



    //Google Maps
    implementation("com.google.maps.android:maps-compose:6.6.0")
    implementation("com.google.maps.android:maps-compose-widgets:6.6.0")
    implementation("com.google.maps.android:maps-compose-utils:6.6.0")

    //Google Maps Current Location
//    implementation(libs.play.services.location)

//    //Directions
//    implementation("com.google.android.gms:play-services-maps:19.1.0")

    // Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
//    implementation("com.google.android.gms:play-services-location")


    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    //Coroutine Android SDK
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    //extended icon dependency
    implementation("androidx.compose.material:material-icons-extended")

    //navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //google places autocomplete
    implementation("com.google.android.libraries.places:places:3.4.0")

    //razorpay integration
    implementation("com.razorpay:checkout:1.5.17")

    implementation("com.google.android.gms:play-services-tasks:18.1.0")



}