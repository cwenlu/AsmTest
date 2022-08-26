import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @Author cwl
 * @Date 2022/8/26 10:05 上午
 * @Description 简单演示
 */
abstract class SimplePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.finalizeDsl {
            println("change successfully")
            it.defaultConfig {
                minSdk = 23
            }
            it.buildTypes.create("extra")
        }
    }
}