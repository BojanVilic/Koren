plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.koren.compose.application)
    alias(libs.plugins.koren.hilt)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.koren"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.koren"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.map)
    implementation(projects.feature.activity)
    implementation(projects.feature.account)
    implementation(projects.feature.invitation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.timber)
    implementation(libs.kotlin.serialization)
    implementation(libs.androidx.core.splashscreen)
}