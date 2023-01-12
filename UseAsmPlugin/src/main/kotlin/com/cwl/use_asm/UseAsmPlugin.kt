package com.cwl.use_asm

import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor

/**
 * [custom plugin](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:custom_plugins_standalone_project)
 * @Author cwl
 * @Date 2022/12/28 10:38 上午
 * @Description
 */
class UseAsmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants {
            it.instrumentation.transformClassesWith(
                UseAsmVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) {}

            it.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
        }
    }
}

abstract class UseAsmVisitorFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ClickClassVisitor(nextClassVisitor)
    }
    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}

//AppPlugin
//与com.android.application一起应用的插件,可判断是否是android app

//LibraryPlugin
//与com.android.library一起应用的插件,可判断是否是android lib

//AndroidComponentsExtension
//Android Gradle 插件相关组件的通用扩展。 每个组件都有一个类型，如应用程序或库，并且将具有与特定组件类型相关的方法的专用扩展

//ApplicationAndroidComponentsExtension
//Android Application Gradle Plugin 组件的扩展。这是应用 com.android.application 插件时的 androidComponents 块
//只有Android Gradle插件才能在com.android.build.api.variant中创建接口实例。

//LibraryAndroidComponentsExtension
//Android Library Gradle 插件组件的扩展。这是应用 com.android.library 插件时的 androidComponents 块
//只有Android Gradle插件才能在com.android.build.api.variant中创建接口实例

//project.plugins.withType(AppPlugin::class.java) {
//    val extension = project.extensions.getByName("androidComponents") as ApplicationAndroidComponentsExtension
//}
//
//project.plugins.withType(LibraryPlugin::class.java) {
//    val extension = project.extensions.getByName("androidComponents") as LibraryAndroidComponentsExtension
//}