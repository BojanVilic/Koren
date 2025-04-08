plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.notifications"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.firebase.messaging.ktx)
}