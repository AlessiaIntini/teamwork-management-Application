plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleGmsGoogleServices)
    id("kotlin-android")
    id("kotlin-parcelize")
}

android {
    namespace = "it.polito.teamhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "it.polito.teamhub"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-alpha01"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.firebase.auth.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.animation.v110)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.ktx.v170)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.v100)
    implementation(libs.androidx.graphics.core)
    implementation(libs.androidx.graphics.path)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.gson)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material.v105)
    implementation(libs.androidx.material.v110alpha05)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.v260)
    implementation(libs.composecalendar)
    implementation(libs.mpandroidchart)
    implementation(libs.runtime.livedata)
    implementation(libs.ucrop)
    implementation(libs.ui.tooling)
    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    implementation(libs.core)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}