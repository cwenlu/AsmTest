import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * @Author cwl
 * @Date 2022/8/26 1:45 下午
 * @Description 给android script 添加自定义对象,进行读取使用
 */
abstract class CustomAgpDslPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val isApp = target.plugins.hasPlugin(AppPlugin::class.java)
        val isLib = target.plugins.hasPlugin(LibraryPlugin::class.java)
        if (isApp) {
            val android = target.extensions.getByType(ApplicationExtension::class.java)
            android.buildTypes.forEach {
                (it as ExtensionAware).extensions.add("paramsDsl", BuildTypeExtension::class.java)
            }
        }
        if (isLib) {
            val androidLib = target.extensions.getByType(LibraryExtension::class.java)
            androidLib.buildTypes.forEach {
                it.extensions.add("paramsDsl", BuildTypeExtension::class.java)
            }

            val androidComponents =
                target.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponents.onVariants {
                //这个回调执行了4次,因为配置了2个flavor,然后因为有2个buildType
                println("buildType:${it.buildType},variant:${it.name}")

                //通过variant.name 获取buildType
                //下面这样如果配置了Flavor获取会有问题,应该使用it.buildType
                //val buildTypeDsl = androidLib.buildTypes.getByName(it.name)
                val buildTypeDsl = androidLib.buildTypes.getByName(it.buildType ?: "")
                val buildTypeExtension =
                    (buildTypeDsl as ExtensionAware).extensions.findByName("paramsDsl") as BuildTypeExtension

                target.tasks.register("${it.name}UseParam", UseParamTask::class.java) {
                    group = "test"
                    parameters.set(buildTypeExtension.invocationParameters ?: "")
                }
            }
        }


    }
}

interface BuildTypeExtension {
    var invocationParameters: String?
}

abstract class UseParamTask : DefaultTask() {

    @get:Input
    abstract val parameters: Property<String>

    @TaskAction
    fun taskAction() {
        println("use paramsDsl value:${parameters.get()}")
    }
}