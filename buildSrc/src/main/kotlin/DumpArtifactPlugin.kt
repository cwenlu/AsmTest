import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifactsLoader
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * @Author cwl
 * @Date 2022/8/29 10:24 上午
 * @Description
 * agp 是对gradle transform action 的封装 https://docs.gradle.org/current/userguide/artifact_transforms.html
 */
abstract class DumpArtifactPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val androidComponets = target.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponets.onVariants { variant ->
            target.tasks.register("${variant.name}DumpArtifacts", DumpArtifactTask::class.java) {
                group = "test"
                artifactFolder.set(variant.artifacts.get(SingleArtifact.APK))
                builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
            }
        }
    }
}

abstract class DumpArtifactTask : DefaultTask() {
    @get:InputFiles
    abstract val artifactFolder: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @TaskAction
    fun taskAction() {
        val builtArtifacts = builtArtifactsLoader.get().load(artifactFolder.get())
            ?: throw RuntimeException("Cannot load artifacts")
        builtArtifacts.elements.forEach {
            //LogLevel.QUIET 这个级别输出了，info，debug试了没输出
            logger.log(LogLevel.QUIET, it.outputFile)
        }
    }
}