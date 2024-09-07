import com.android.build.api.dsl.ApplicationExtension
import com.bojanvilic.koren.configureComposeSetup
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.getByType<ApplicationExtension>()
            configureComposeSetup(extension)
        }
    }
}