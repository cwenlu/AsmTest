package com.cwl.use_asm.util

import org.objectweb.asm.tree.MethodNode

/**
 * @Author cwl
 * @Date 2022/12/29 4:25 下午
 * @Description
 */


/**
 * 组合name+desc 可以用于唯一定位方法
 */
val MethodNode.nameWithDesc:String
    get() = name+desc

/**
 * 是否包含某个注解,包含返回true 反之返回false
 * 如要判断是否包含androidx.annotation.Nullable,则desc为 Landroidx/annotation/Nullable;
 * @receiver MethodNode
 * @param desc String 签名
 * @return Boolean
 */
fun MethodNode.hasAnnotation(desc: String): Boolean {
    return visibleAnnotations?.find { it.desc == desc } != null
}

