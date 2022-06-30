package com.example.timestatisticplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.example.mt.MethodTimerTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class TimeStatistics implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin.class)
        if (isApp) {
            def appExtension = project.getExtensions().getByType(AppExtension.class)
            appExtension.registerTransform(new MethodTimerTransform())

            def statisticsExtension = project.extensions.create('statistics', StatisticsExtension)
            project.afterEvaluate {
                def burPoint = statisticsExtension.buryPoint
                if (burPoint!=null){
                    burPoint.each{
                        it.each{
                            println it.key+'---'+it.value
                        }
                    }
                }
            }
        }
    }
}