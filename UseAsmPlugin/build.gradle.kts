plugins {
    kotlin("jvm") version "1.7.0"
    id("java-gradle-plugin")
    id("maven-publish")
}

dependencies {
    gradleApi()
    implementation(kotlin("stdlib"))
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("com.android.tools.build:gradle:7.2.1") {
        exclude(group = "org.ow2.asm")
    }
}

//https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers

group = "com.cwl"
version = "1.0.0"
//执行publish进行发布，目前不知到怎么配置artifactId,默认模块名字
gradlePlugin {
    plugins {

        //生成规则plugin.id:plugin.id.gradle.plugin:plugin.version
        //eg:use-asm2:use-asm2.gradle.plugin:1.0.0
        create("UseAsm") {
            id = "use-asm2"
            implementationClass = "com.cwl.use_asm.UseAsmPlugin"
        }

    }
}
publishing {
    repositories {
        maven {
            url = uri("../repo")
        }
    }
}
//=======================


//publishing{
//    publications{
//        //publishMavenPublicationToMavenRepository
//        create<MavenPublication>("maven"){
//            from(components["java"])
//
//            groupId = "com.cwl"
//            artifactId = "use-asm"
//            version = "1.0.0"
//        }
//    }
//
//    repositories {
//        maven {
//            url = uri("../repo")
//        }
//    }
//}
