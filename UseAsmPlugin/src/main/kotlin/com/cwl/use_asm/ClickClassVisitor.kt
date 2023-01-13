package com.cwl.use_asm

import com.cwl.use_asm.util.filterLambda
import com.cwl.use_asm.util.nameWithDesc
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

/**
 * @Author cwl
 * @Date 2022/12/29 3:26 下午
 * @Description
 */
class ClickClassVisitor(private val nextClassVisitor: ClassVisitor) : ClassNode(Opcodes.ASM9) {
    private companion object {
        const val CLICK_METHOD = "onClick"
        const val CLICK_INTERFACE = "android/view/View\$OnClickListener"
        const val CLICK_INTERFACE_DESC = "Landroid/view/View\$OnClickListener;"
        const val CLICK_METHOD_NAME_DESC = "onClick(Landroid/view/View;)V"
        const val VIEW_DESC = "Landroid/view/View;"
    }

    override fun visitEnd() {
        super.visitEnd()
        val shouldHookMethodList = mutableSetOf<MethodNode>()
        val haveInnerClass = interfaces.contains(CLICK_INTERFACE)
        methods.forEach {
            if (haveInnerClass && it.nameWithDesc == CLICK_METHOD_NAME_DESC) {
                shouldHookMethodList.add(it)
            }
            val dynamicInsnNodes = it.filterLambda {
                //onClick:()Landroid/view/View$OnClickListener;
                //方法可能引用外部实例，所以利用endsWith判断
                it.name == CLICK_METHOD && it.desc.endsWith(CLICK_INTERFACE_DESC)
            }

            //BootstrapMethods:
            //0: #43 REF_invokeStatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
            //Method arguments:
            //#32 (Landroid/view/View;)V
            //#36 REF_invokeStatic com/example/asmtest/MainActivity."onCreate$lambda-0":(Landroid/view/View;)V
            //#32 (Landroid/view/View;)V

            //bsmArgs[1] 为org.objectweb.asm.Handle  是自动生成的方法
            //其余两个为org.objectweb.asm.Type
            dynamicInsnNodes.forEach {
                val handle = it.bsmArgs[1] as? Handle
                if (handle != null) {
                    val nameWithDesc = handle.name + handle.desc
                    shouldHookMethodList.addAll(methods.filter { it.nameWithDesc == nameWithDesc })
                }
            }


        }
        shouldHookMethodList.forEach {
            timeCost(name, it)
        }
        accept(nextClassVisitor)
    }

    /**
     * 针对点击事件的方法耗时计算
     * @param name String
     * @param methodNode MethodNode
     */
    private fun timeCost(name: String, methodNode: MethodNode) {
        //查找某个参数的索引
        ////获取方法type
        //val argumentsTypes = Type.getArgumentTypes(methodNode.desc)
        ////找到 VIEW_DESC 参数的索引
        //val viewArgumentIndex = argumentsTypes.indexOfFirst {
        //    it.descriptor == VIEW_DESC
        //}

        //判断不是frame 指令认为是方法体开头
        val insnNodeStart =
            methodNode.instructions.firstOrNull { it.opcode > Opcodes.F_SAME1 }
        //判断是return指令认为是方法结尾
        val insnNodeEnd = methodNode.instructions.reversed()
            .firstOrNull { (Opcodes.IRETURN <= it.opcode) and (it.opcode <= Opcodes.RETURN) }

        val insnListBefore = InsnList().apply {
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/System",
                    "currentTimeMillis",
                    "()J",
                    false
                )
            )
            add(VarInsnNode(Opcodes.LSTORE, 1))
        }
        val insnListAfter = InsnList().apply {
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/System",
                    "currentTimeMillis",
                    "()J",
                    false
                )
            )
            add(VarInsnNode(Opcodes.LLOAD, 1))
            add(InsnNode(Opcodes.LSUB))
            add(VarInsnNode(Opcodes.LSTORE, 1))

            add(
                FieldInsnNode(
                    Opcodes.GETSTATIC,
                    "java/lang/System",
                    "out",
                    "Ljava/io/PrintStream;"
                )
            )
            add(TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"))
            add(InsnNode(Opcodes.DUP))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/StringBuilder",
                    "<init>",
                    "()V",
                    false
                )
            )
            add(LdcInsnNode("${name}/${methodNode.name} --> 耗时 :"))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                )
            )
            add(VarInsnNode(Opcodes.LLOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(J)Ljava/lang/StringBuilder;"
                )
            )
            add(LdcInsnNode("ms"))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "toString",
                    "()Ljava/lang/String;"
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    "println",
                    "(Ljava/lang/String;)V"
                )
            )
        }

        methodNode.instructions.insertBefore(insnNodeStart, insnListBefore)
        methodNode.instructions.insertBefore(insnNodeEnd, insnListAfter)
    }

    //private fun dumpMethod(methodNode: MethodNode) {
    //    val textifier = Textifier()
    //    //methodNode.accept(TraceMethodVisitor(methodNode, textifier))
    //    //val sw = StringWriter()
    //    //val pw = PrintWriter(sw)
    //    //textifier.print(pw)
    //    //println(sw.toString())
    //
    //    val pw = PrintWriter(System.out)
    //    textifier.print(pw)
    //}
}