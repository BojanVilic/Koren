plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.onboarding"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.data)

    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    implementation(libs.lottie.compose)
}