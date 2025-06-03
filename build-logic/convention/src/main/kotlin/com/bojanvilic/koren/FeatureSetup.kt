package com.bojanvilic.koren

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

internal fun Project.configureFeatureModule(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    setTargetSdkVersion: (Int) -> Unit
) {
    setTargetSdkVersion(libs.findVersion("targetSdkVersion").get().requiredVersion.toInt())

    commonExtension.apply {
        compileSdk = libs.findVersion("targetSdkVersion").get().requiredVersion.toInt()

        defaultConfig {
            minSdk = libs.findVersion("minSdkVersion").get().requiredVersion.toInt()
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }

        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")

            jvmTarget = JavaVersion.VERSION_17.toString()
        }

        buildFeatures {
            buildConfig = true
        }
    }
}