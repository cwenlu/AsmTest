import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.TaskAction

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

        //androidComponents.onVariants {
        //    target.tasks.register("${it.name}PrintClasspath", PrintClasspathTask::class.java){
        //        classpath.from(it.compileClasspath)
        //    }
        //}
    }
}

abstract class PrintClasspathTask : DefaultTask() {
    @get:Classpath
    abstract val classpath: ConfigurableFileCollection

    @TaskAction
    fun taskAction() {
        for (file in classpath.files) {
            println(file.absolutePath)
        }
    }

}