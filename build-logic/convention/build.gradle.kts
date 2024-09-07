plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "koren.hilt"
            implementationClass = "HiltConventionPlugin"
        }

        register("composeApplicationSetup") {
            id = "koren.compose.application"
            implementationClass = "compose.ComposeApplicationConventionPlugin"
        }

        register("composeLibrarySetup") {
            id = "koren.compose.library"
            implementationClass = "compose.ComposeLibraryConventionPlugin"
        }

        register("featureSetup") {
            id = "koren.feature"
            implementationClass = "FeatureConventionPlugin"
        }

        register("coreSetup") {
            id = "koren.core"
            implementationClass = "CoreConventionPlugin"
        }
    }
}