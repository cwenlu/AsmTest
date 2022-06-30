package com.example.mt

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class MethodTimerTransform extends Transform {

    @Override
    String getName() {
        return 'timer'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        if (!incremental) {
            outputProvider.deleteAll()
        }

        inputs.each {
            it.directoryInputs.each {
                handleDirectoryInput(it, outputProvider)
            }
        }
    }

    void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse {
                def name = it.name
                if (filterClass(name)) {
                    def classReader = new ClassReader(it.bytes)
                    def classWriter=new ClassWriter(classreader,ClassWriter.COMPUTE_MAXS)

                }
            }
        }
    }

    boolean filterClass(String className) {
        return className.endsWith('.class')
                && !className.startsWith('R\$')
                && className != 'R.class'
                && className != 'BuildConfig.class'
    }
}