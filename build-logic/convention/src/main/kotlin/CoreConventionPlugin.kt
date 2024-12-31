
import com.android.build.gradle.LibraryExtension
import com.bojanvilic.koren.configureKotlinAndroid
import com.bojanvilic.koren.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class CoreConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
                add("implementation", libs.findLibrary("timber").get())
            }
        }
    }
}