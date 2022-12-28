package com.cwl.kt_test

/**
 * @Author cwl
 * @Date 2022/8/30 5:34 下午
 * @Description 模拟编译时注解动态生成的处理类
 */
interface IWebCallProcessor {
    fun process(value: Int): Boolean
}

/**
 * 处理奇数
 */
class OneProcessor : IWebCallProcessor {
    override fun process(value: Int): Boolean {
        println("value: ${value} made by ${this::class.simpleName}")
        return value % 2 != 0
    }
}

/**
 * 处理偶数
 */
class TwoProcessor : IWebCallProcessor {
    override fun process(value: Int): Boolean {
        println("value: ${value} made by ${this::class.simpleName}")
        return value % 2 == 0
    }
}