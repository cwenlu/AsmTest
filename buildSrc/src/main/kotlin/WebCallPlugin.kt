import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM5
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode
import java.util.*

/**
 * @Author cwl
 * @Date 2022/8/31 3:32 下午
 * @Description
 */
abstract class WebCallPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants {
            it.instrumentation.run {
                //怎么处理先后顺序
                transformClassesWith(
                    FindWebCallProcessorVisitor::class.java,
                    InstrumentationScope.PROJECT
                ) {}
                transformClassesWith(
                    WebCallOwnerVisitor::class.java,
                    InstrumentationScope.PROJECT
                ) {}
                setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
            }
        }
    }
}

object FindWebCallProcessorHelper {
    val webCallProcessorList = arrayListOf<String>()

}

abstract class FindWebCallProcessorVisitor :
    AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        //return object : ClassVisitor(ASM5, nextClassVisitor) {
        //    override fun visit(
        //        version: Int,
        //        access: Int,
        //        name: String?,
        //        signature: String?,
        //        superName: String?,
        //        interfaces: Array<out String>?
        //    ) {
        //        name?.let { FindWebCallProcessorHelper.webCallProcessorList.add(it) }
        //        println(name + "====")
        //        super.visit(version, access, name, signature, superName, interfaces)
        //    }
        //}
        return object : ClassNode(ASM5) {
            override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String>?
            ) {
                super.visit(version, access, name, signature, superName, interfaces)
                name?.let { FindWebCallProcessorHelper.webCallProcessorList.add(it) }
                println(name + "===="+Arrays.toString(FindWebCallProcessorHelper.webCallProcessorList.toTypedArray()))
            }

            override fun visitEnd() {
                super.visitEnd()
                methods
            }
        }
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className.contains("com.cwl.kt_test") && classData.interfaces.contains("com.cwl.kt_test.IWebCallProcessor")
    }
}

abstract class WebCallOwnerVisitor : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val className = classContext.currentClassData.className
        return object : ClassVisitor(ASM5, nextClassVisitor) {
            override fun visitMethod(
                access: Int,
                name: String?,
                descriptor: String?,
                signature: String?,
                exceptions: Array<out String>?
            ): MethodVisitor {
                var methodVisitor =
                    super.visitMethod(access, name, descriptor, signature, exceptions)
                if (className == "com.cwl.kt_test.WebCallOwner" && "<clinit>" == name) {
                    methodVisitor =
                        MethodWebCallOwnerAdviceAdapter(methodVisitor, access, name, descriptor)
                }
                return methodVisitor
            }
        }
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className == "com.cwl.kt_test.WebCallOwner"
    }
}

class MethodWebCallOwnerAdviceAdapter(
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(ASM5, methodVisitor, access, name, descriptor){
    override fun onMethodEnter() {
        super.onMethodEnter()
        println(FindWebCallProcessorHelper.webCallProcessorList.size.toString() + "====")

    }
}
