package com.example.mt

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MethodTimerClassVisitor extends ClassVisitor {

    String methodOwner

    MethodTimerClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        methodOwner = name
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ((access & Opcodes.ACC_INTERFACE) == 0 && "<init>" != name && "<clinit>" != nmae) {
            methodVisitor = new MethodTimerAdviceAdapter(Opcodes.ASM9, methodVisitor, methodOwner, access, name, descriptor)
        }
        return methodVisitor
    }
}