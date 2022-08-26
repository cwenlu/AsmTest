plugins {
    //KotlinDslPlugin 这个插件包含了另外3个插件
    //java-gradle-plugin (直接配置发布插件)
    //kotlin-dsl.base (kotlin相关环境配置)
    //kotlin-dsl.precompiled-script-plugins (支持*.gradle.kts当插件使用)
    `kotlin-dsl`

    //kotlin("jvm") version "1.7.0"
}

repositories {
    google()
    mavenCentral()
}

dependencies{
    implementation("com.android.tools.build:gradle:7.2.1")
    //gradleApi()
}