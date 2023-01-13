package com.cwl.use_asm.util

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * @Author cwl
 * @Date 2022/12/29 4:25 下午
 * @Description Asm Tree api 帮助类
 */


val MethodNode.isStatic: Boolean
    get() = access and Opcodes.ACC_STATIC != 0

val MethodNode.isInitMethod: Boolean
    get() = name == "<init>"

/**
 * 组合name+desc 可以用于唯一定位方法
 */
val MethodNode.nameWithDesc: String
    get() = name + desc

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

/**
 * 查找lambda
 * @receiver MethodNode
 * @param predicate Function1<InvokeDynamicInsnNode, Boolean>
 * @return List<InvokeDynamicInsnNode>
 */
fun MethodNode.filterLambda(predicate: (InvokeDynamicInsnNode) -> Boolean): List<InvokeDynamicInsnNode> {
    instructions ?: return emptyList()
    val dynamicList = mutableListOf<InvokeDynamicInsnNode>()
    instructions.forEach {
        if (it is InvokeDynamicInsnNode) {
            if (predicate(it)) {
                dynamicList.add(it)
            }
        }
    }
    return dynamicList
}

/**
 * 根据指定[predicate]获取参数索引
 * @receiver MethodNode
 * @param predicate Function1<Array<Type>, Int>
 * @return Int
 */
fun MethodNode.argumentIndexOf(predicate: (Array<Type>) -> Int): Int {
    val argumentTypes = Type.getArgumentTypes(desc)
    return predicate(argumentTypes)
}

/**
 * 返回第一个符合[desc]的参数索引
 * @receiver MethodNode
 * @param desc String
 */
fun MethodNode.argumentIndexOfFirst(desc: String): Int {
    return argumentIndexOf {
        it.indexOfFirst {
            it.descriptor == desc
        }
    }
}

/**
 * 获取指定[argumentIndex]的slot位置
 * @receiver MethodNode
 * @param argumentIndex Int
 * @param argumentTypes Array<Type>
 * @return Int 错误返回-1 正确返回 >=0
 */
fun MethodNode.slotIndex(
    argumentIndex: Int,
    argumentTypes: Array<Type> = Type.getArgumentTypes(desc)
): Int {
    if (argumentIndex < 0 || argumentIndex >= argumentTypes.size) return -1
    return if (argumentIndex == 0) {
        if (isStatic) 0 else 1
    } else {
        slotIndex(argumentIndex - 1, argumentTypes) + argumentTypes[argumentIndex - 1].size
    }
}

/**
 * 获取第一个满足[desc]参数的slot
 * @receiver MethodNode
 * @param desc String
 * @return Int
 */
fun MethodNode.slotIndexOfFirstDesc(desc: String): Int {
    val argumentTypes = Type.getArgumentTypes(this.desc)
    val index = argumentTypes.indexOfFirst {
        it.descriptor == desc
    }
    return slotIndex(index)
}

/**
 * 生成一个方法中指定[type]该使用的slot
 * @receiver MethodNode
 * @param type Type
 * @return Int
 */
fun MethodNode.newSlotIndex(type: Type): Int {
    val argumentTypes = Type.getArgumentTypes(desc)
    return slotIndex(argumentTypes.size - 1, argumentTypes) + type.size
}