package compose

import com.android.build.api.dsl.ApplicationExtension
import com.bojanvilic.koren.configureComposeSetup
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ComposeApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.getByType<ApplicationExtension>()
            configureComposeSetup(extension)
        }
    }
}