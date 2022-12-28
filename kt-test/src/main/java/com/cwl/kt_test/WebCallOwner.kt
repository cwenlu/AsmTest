package com.cwl.kt_test

import kotlin.random.Random

/**
 * @Author cwl
 * @Date 2022/8/30 5:40 下午
 * @Description
 */
object WebCallOwner {
    private val processorList = arrayListOf<IWebCallProcessor>()

    init {
        val s="afaafafa"
        processorList.add(OneProcessor())
        val ss="fafafa"
    }

    fun process() {
        for (iWebCallProcessor in processorList) {
            val res = iWebCallProcessor.process(Random.nextInt(100))
            if (res){
                break
            }
        }
    }
}