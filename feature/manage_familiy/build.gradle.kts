plugins {
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.carfax.manage_familiy"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
}