plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.map"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)

    implementation(libs.maps.compose)

    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.location)
    implementation(libs.play.services.places)
    implementation(libs.places)
    implementation(libs.play.services.fitness)

    implementation(libs.android.maps.utils)
    implementation(libs.firebase.database.ktx)


    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.common)
}