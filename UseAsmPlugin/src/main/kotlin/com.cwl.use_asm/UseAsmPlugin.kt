package com.cwl.use_asm

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * [custom plugin](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:custom_plugins_standalone_project)
 * @Author cwl
 * @Date 2022/12/28 10:38 上午
 * @Description
 */
class UseAsmPlugin:Plugin<Project> {
    override fun apply(target: Project) {
        println("kooooooo")
    }
}