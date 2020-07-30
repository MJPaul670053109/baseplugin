package kidswant.architecture.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * @author MJ on 2020-1-014
 */
class KwAppTransForm extends Transform {

    KwAppTransForm() {

    }

    @Override
    String getName() {
        return "ASM"
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
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        KwPluginSingleInstance.getInstance().getPlugins().clear()
        transformInvocation.inputs.each {
            it.directoryInputs.each {
                if (it.file.isDirectory()) {
                    it.file.eachFileRecurse {
                        def fileName = it.name
//                        println("------------------" + fileName + "-----------------")
                        if (fileName.endsWith(".class") && !fileName.startsWith("R\$")
                                && fileName != "BuildConfig.class" && fileName != "R.class") {
                            //各种过滤类，关联classVisitor
//                            handleFile(it)
                        }
                    }
                }
                def dest = transformInvocation.outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, dest)
            }
            it.jarInputs.each { jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                File src = jarInput.file
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                scanJar(src, dest)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        //注入代码
        KwRegisterCodeGenerator.insertInitCodeTo(KwPluginSingleInstance.getInstance().targetClass, KwPluginSingleInstance.getInstance().targetClassJarFile)
    }


    static void scanJar(File jarFile, File dest) {
        if (jarFile) {
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (entryName.endsWith(".class")) {
                    InputStream inputStream = file.getInputStream(jarEntry)
                    scanClass(inputStream, dest)
                    inputStream.close()
                }
            }
            file.close()
        }
    }

    static void scanClass(InputStream inputStream, File dest) {
        def cr = new ClassReader(inputStream)
        def cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        def classVisitor = new KwMethodFind(Opcodes.ASM5, cw, dest)
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }
}
