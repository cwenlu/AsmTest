/**
 * [api](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:working_with_files_in_custom_tasks_and_plugins)
 *
 * 单独开文件写提示有些问题
 */

abstract class InfoToFileTask : DefaultTask() {
    @get:org.gradle.api.tasks.OutputFile
    abstract val destination: RegularFileProperty

    @org.gradle.api.tasks.TaskAction
    fun taskAction() {
        val file = destination.get().asFile
        file.parentFile.mkdirs()
        file.writeText("write info to file")
    }
}

val infoToFile = tasks.register<InfoToFileTask>("infoToFile") {
    group = "test"
    //创建一个RegularFileProperty
    val infoFile = objects.fileProperty()
    infoFile.set(layout.buildDirectory.file("info.txt"))
    destination.set(infoFile)
}

tasks.register("dumpInfoFromFile") {
    group = "test"

    dependsOn("infoToFile")

    doLast {
        val info = infoToFile.map { it.destination.get().asFile.readText() }.get()
        println("info from file:${info}")

    }
}