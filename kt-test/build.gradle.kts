plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("script-plugin")
}
apply<CustomAgpDslPlugin>()
apply<ExtendConfigPlugin>()
apply(from = "use-file-plugin.gradle.kts")

//只有一个属性设置可以使用这种
the<ExtendConfig>().message = "sas"

//多个可以使用这种
configure<ExtendConfig> {
    message = "set message"
    //info.set("set info")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("boolean", "showLog", "true")
        //resources.getBoolean(R.bool.dark)
        resValue("bool", "dark", "true")
        resValue("string", "name", "cwl")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            the<BuildTypeExtension>().invocationParameters = "-debug -log"
        }
    }
    setFlavorDimensions(arrayListOf("flavor-cwl"))
    productFlavors {
        create("pf1") {
            dimension = "flavor-cwl"
        }
        create("pf2") {
            dimension = "flavor-cwl"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
}

abstract class GitVersionTask : DefaultTask() {

    @get:OutputFile
    abstract val gitVersionOutputFile: RegularFileProperty

    @ExperimentalStdlibApi
    @TaskAction
    fun taskAction() {

        // this would be the code to get the tip of tree version,
        // val firstProcess = ProcessBuilder("git","rev-parse --short HEAD").start()
        // val error = firstProcess.errorStream.readBytes().decodeToString()
        // if (error.isNotBlank()) {
        //      System.err.println("Git error : $error")
        // }
        // var gitVersion = firstProcess.inputStream.readBytes().decodeToString()

        // but here, we are just hardcoding :
        gitVersionOutputFile.get().asFile.writeText("1234")
    }
}


val gitVersionProvider = tasks.register<GitVersionTask>("gitVersionProvider") {
    File(project.buildDir, "intermediates/gitVersionProvider/output").also {
        it.parentFile.mkdirs()
        gitVersionOutputFile.set(it)
    }
    outputs.upToDateWhen { false }
}

abstract class ManifestReaderTask : DefaultTask() {
    @get:org.gradle.api.tasks.InputFile
    abstract val mergedManifest: RegularFileProperty

    @org.gradle.api.tasks.TaskAction
    fun taskAction() {
        val manifest = mergedManifest.asFile.get().readText()
        //确保已经替换成功
        if (!manifest.contains("activity android:name=\"com.cwl.kt_test.DynamicActivity\"")) {
            throw RuntimeException("Manifest Placeholder not replaced successfully")
        }
    }
}

abstract class StringProducerTask : DefaultTask() {

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @ExperimentalStdlibApi
    @TaskAction
    fun taskAction() {
        outputFile.get().asFile.writeText("android.intent.action.MAIN")
    }
}


val androidNameProvider = tasks.register<StringProducerTask>("androidNameProvider") {
    File(project.buildDir, "intermediates/androidNameProvider/output").also {
        it.parentFile.mkdirs()
        outputFile.set(it)
    }
    outputs.upToDateWhen { false }
}


abstract class UpdateArtifactTask : DefaultTask() {
    @get: InputFiles
    abstract val initialArtifact: RegularFileProperty

    @get: OutputFile
    abstract val updatedArtifact: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val versionCode = "artifactTransformed = true"
        println("artifactPresent = " + initialArtifact.isPresent)
        updatedArtifact.get().asFile.writeText(versionCode)
    }
}

abstract class ConsumeArtifactTask : DefaultTask() {
    @get: InputFiles
    abstract val finalArtifact: RegularFileProperty

    @TaskAction
    fun taskAction() {
        println(finalArtifact.get().asFile.readText())
    }
}

abstract class ManifestTransformerTask : DefaultTask() {
    @get:org.gradle.api.tasks.Input
    abstract val activityName: Property<String>

    @get:org.gradle.api.tasks.InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:org.gradle.api.tasks.OutputFile
    abstract val updatedManifest: RegularFileProperty

    @org.gradle.api.tasks.TaskAction
    fun taskAction() {
        println("read activityName " + activityName.get())
        var manifest = mergedManifest.asFile.get().readText()
        //添加一个网络权限
        manifest = manifest.replace(
            "<application",
            "<uses-permission android:name=\"android.permission.INTERNET\"/>\n<application"
        )
        println("writes to " + updatedManifest.asFile.get().absolutePath)
        updatedManifest.get().asFile.writeText(manifest)
    }
}

abstract class ManifestProducerTask : DefaultTask() {
    @get:org.gradle.api.tasks.InputFile
    abstract val gitInfoFile: RegularFileProperty

    @get:org.gradle.api.tasks.OutputFile
    abstract val outputManifest: RegularFileProperty

    @org.gradle.api.tasks.TaskAction
    fun taskAction() {
        val gitVersion = gitInfoFile.get().asFile.readText()
        val manifest = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="com.cwl.kt_test"
                android:versionName="${gitVersion}">

                <uses-sdk
                    android:minSdkVersion="21"
                    android:targetSdkVersion="32" />

                <application>
                    <activity android:name="com.cwl.kt_test.DynamicActivity" />
                </application>

            </manifest>
        """.trimIndent()
        println("write to " + outputManifest.get().asFile.absolutePath)
        outputManifest.get().asFile.writeText(manifest)
    }
}

//androidComponents {
//    val debug = selector().withFlavor(Pair("flavor-cwl","pf1"))
//    //找到指定的
//    //beforeVariants(debug){
//    //    println("debug with variant : ${it.name}")
//    //}
//
//    //beforeVariants {
//    //    println("all with variant : ${it.name}--${it.buildType}--${it.flavorName}--${it.productFlavors.toString()}")
//    //}
//
//    beforeVariants(debug){
//        println("ss-->${it.name}")
//    }
//}

androidComponents {
    //finalizeDsl {
    //    it.compileSdk
    //    it.defaultConfig {
    //        minSdk = 10
    //        targetSdk = 30
    //    }
    //    //it.buildTypes.create("extra").let {
    //    //    it.isMinifyEnabled = true
    //    //}
    //}

    //beforeVariants{
    //    if(it.buildType=="debug"){
    //        it.enableAndroidTest=false
    //
    //        it.enable = false
    //    }
    //
    //}
    onVariants {
        //gradle 最上面需要导包 import com.android.build.api.variant.BuildConfigField
        //对应android中的buildConfigField("boolean","showLog","true")
        //it.buildConfigFields.put("FloatValue", BuildConfigField("Float", "1f", "Float Value" ))
        //it.buildConfigFields.put("LongValue", BuildConfigField("Long", "1L", "Long Value" ))

        //it.buildConfigFields.put("GitVersion", gitVersionProvider.map {  task ->
        //    com.android.build.api.variant.BuildConfigField(
        //        "String",
        //        "\"${task.gitVersionOutputFile.get().asFile.readText(Charsets.UTF_8)}\"",
        //        "Git Version"
        //    )
        //})

        //tasks.register<ManifestReaderTask>("${it.name}ManifestReader"){
        //    mergedManifest.set(it.artifacts.get(com.android.build.api.artifact.SingleArtifact.MERGED_MANIFEST))
        //}
        //it.manifestPlaceholders.put("dynamicName","DynamicActivity")

        //it.manifestPlaceholders.put("MyName", androidNameProvider.flatMap { task ->
        //    task.outputFile.map { it.asFile.readText(Charsets.UTF_8) }
        //})

        //it.resValues.put(it.makeResValueKey("string","VariantName"), ResValue(name,"VariantName"))

        //val updateArtifact = project.tasks.register<UpdateArtifactTask>("${it.name}UpdateArtifact")
        //project.tasks.register<ConsumeArtifactTask>("${it.name}ConsumeArtifact") {
        //    finalArtifact.set(it.artifacts.get(com.android.build.api.artifact.SingleArtifact.AAR))
        //}
        //it.artifacts.use(updateArtifact)
        //    .wiredWithFiles(
        //        UpdateArtifactTask::initialArtifact,
        //        UpdateArtifactTask::updatedArtifact)
        //    .toTransform(com.android.build.api.artifact.SingleArtifact.AAR)

        //val manifestUpdater = tasks.register<ManifestTransformerTask>("${it.name}ManifestUpdater") {
        //    activityName.set("AddActivity")
        //}
        //it.artifacts.use(manifestUpdater).wiredWithFiles(
        //    ManifestTransformerTask::mergedManifest,
        //    ManifestTransformerTask::updatedManifest
        //).toTransform(com.android.build.api.artifact.SingleArtifact.MERGED_MANIFEST)

        val manifestProducer = tasks.register<ManifestProducerTask>("${it.name}ManifestProducer") {
            gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
        }
        //use一个基于task的访问操作
        it.artifacts.use(manifestProducer).wiredWith(ManifestProducerTask::outputManifest)
            .toCreate(com.android.build.api.artifact.SingleArtifact.MERGED_MANIFEST)
    }
}
