import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author cwl
 * @Date 2022/8/29 1:34 下午
 * @Description 方法耗时插件
 */
abstract class TimeCostPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants {
            it.instrumentation.transformClassesWith(
                TimeCostVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) {}

            it.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
        }
    }
}

abstract class TimeCostVisitorFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return TimeCostClassVisitor(nextClassVisitor, classContext.currentClassData.className)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className.contains("com.example")
    }
}

class TimeCostClassVisitor(nextVisitor: ClassVisitor, private val className: String) :
    ClassVisitor(Opcodes.ASM5, nextVisitor) {
    private var methodOwner: String = ""
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        methodOwner = name ?: ""
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ((access and Opcodes.ACC_ABSTRACT) == 0 && "<init>" != name && "<clinit>" != name) {
            methodVisitor =
                MethodTimeAdviceAdapter(methodVisitor, methodOwner, access, name, descriptor)
        }
        return methodVisitor
    }
}

class MethodTimeAdviceAdapter(
    methodVisitor: MethodVisitor,
    private val methodOwner: String,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(ASM5, methodVisitor, access, name, descriptor) {
    private var methodName: String = ""
    var slotIndex: Int = -1
    override fun onMethodEnter() {
        super.onMethodEnter()
        methodName = name
        //创建一个局部变量
        slotIndex = newLocal(Type.LONG_TYPE)
        //执行一个静态方法
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        //将栈顶的值存到局部变量里（这里就是上面执行方法的返回值）
        mv.visitVarInsn(LSTORE, slotIndex)
    }

    override fun onMethodExit(opcode: Int) {
        //栈顶的值 L2
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        //将slotIndex 的局部变量load到栈顶 L1
        mv.visitVarInsn(LLOAD, slotIndex)
        //L2-L1
        mv.visitInsn(LSUB)
        //结果存到局部变量
        mv.visitVarInsn(LSTORE, slotIndex)

        //获取System的静态属性,out
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        //new StringBuilder
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
        //复制栈顶数值并将复制值压入栈顶
        //https://blog.csdn.net/liuguang212/article/details/115702243
        //初始化指令(NEW)会使当前对象的引用出栈，如果不复制一份，操作数栈中就没有当前对象的引用了，后面再进行其他的关于这个对象的指令操作时，就无法完成
        mv.visitInsn(DUP)
        //执行StringBuilder的初始化
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        //将给定常量load到栈顶
        mv.visitLdcInsn("$methodOwner/$methodName --> execution time :")
        //执行append
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        mv.visitVarInsn(LLOAD, slotIndex)
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(J)Ljava/lang/StringBuilder;",
            false
        )
        mv.visitLdcInsn("ms")
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "toString",
            "()Ljava/lang/String;",
            false
        )
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        )
        super.onMethodExit(opcode)
    }
}