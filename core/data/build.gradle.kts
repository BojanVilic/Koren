plugins {
    alias(libs.plugins.koren.core)
    alias(libs.plugins.koren.hilt)
}

android {
    namespace = "com.koren.data"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
}