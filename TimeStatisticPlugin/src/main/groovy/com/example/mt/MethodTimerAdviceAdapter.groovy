package com.example.mt

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author cwl* @Date 2021/12/25 9:27 上午
 * @Description
 */

class MethodTimerAdviceAdapter extends AdviceAdapter {

    String methodOwner
    String methodName
    int slotIndex

    MethodTimerAdviceAdapter(int api, MethodVisitor methodVisitor, String owner, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
        methodOwner = owner
        methodName = name
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
        //构造给定类型的局部变量
        slotIndex = newLocal(Type.LONG_TYPE)
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        mv.visitVarInsn(LSTORE, slotIndex)
    }

    @Override
    protected void onMethodExit(int opcode) {
        //执行System的静态方法 System.currentTimeMillis()
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMills", "()J", false)
        //将slotIndex(long)的值load到栈顶
        mv.visitVarInsn(LLOAD, slotIndex)
        //执行减操作
        mv.visitInsn(LSUB)
        //将long型栈顶变量存到slotIndex
        mv.visitVarInsn(LSTORE, slotIndex)
        //将slotIndex(long)的值load到栈顶
        mv.visitVarInsn(LLOAD, slotIndex)
        //将给定常量load到栈顶
        mv.visitLdcInsn(new Long(500))
        //比较栈顶两long型值大小,将结果推到栈顶(依次将栈顶2个元素弹出？)
        mv.visitInsn(LCMP)
        //创建一个字节码位置（分支）
        Label label0 = new Label()
        //将栈顶的值与0比较 （if value <= 0）
        mv.visitJumpInsn(IFLE, label0)
        //获取System的静态属性 System.out
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        //new StringBuilder
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
        //复制栈顶数值并将复制值压入栈顶
        mv.visitInsn(DUP)
        //执行StringBuilder的初始化
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        //将给定常量load到栈顶
        mv.visitLdcInsn(methodOwner + "/" + methodName + " --> execution time : (")
        //执行append
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitVarInsn(LLOAD, slotIndex)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false)
        mv.visitLdcInsn("ms)")
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        //分支结束
        mv.visitLabel(label0)
        super.onMethodExit(opcode)
    }
}