import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

/**
 * [api](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:getting_input_from_the_build)
 * @Author cwl
 * @Date 2022/8/26 4:04 下午
 * @Description 扩展配置
 *
 */
abstract class ExtendConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extendConfig = target.extensions.create<ExtendConfig>("extendConfig")
        val absExt = target.extensions.create<AbsExt>("absExt")
        target.tasks.register("dumpExtendConfig") {
            group = "test"
            doLast {
                println("message:${extendConfig.message},info:${extendConfig.info.orNull}")
                println("absInfo:${absExt.absInfo.get()}")
            }
        }
    }
}

interface ExtendConfig {
    var message: String?

    //这种方式声明的获取的时候如果使用get(),则必须进行配置不然会报错
    //使用orNull,orElse则不会
    val info: Property<String>
}

abstract class AbsExt {
    abstract val absInfo: Property<String>

    init {
        //指定默认值
        //absInfo.set("default absInfo")
        absInfo.convention("default absInfo")
    }
}

//https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_configuration