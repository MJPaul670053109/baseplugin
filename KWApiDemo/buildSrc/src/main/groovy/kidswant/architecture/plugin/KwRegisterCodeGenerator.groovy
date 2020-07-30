package kidswant.architecture.plugin


import jdk.internal.org.objectweb.asm.*
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter
import org.apache.commons.io.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author MJ on 2020-1-14
 * 插入代码到InitPlugin 的init()方法中
 */
class KwRegisterCodeGenerator {

    //需要注入代码的类名
    private String targetClass
    //需要注入代码类的jar包
    private File targetJarClassFile

    private KwRegisterCodeGenerator(String targetClass, File targetJarClassFile) {
        this.targetClass = targetClass
        this.targetJarClassFile = targetJarClassFile
    }

    static void insertInitCodeTo(String targetClass, File targetJarClassFile) {
        KwRegisterCodeGenerator processor = new KwRegisterCodeGenerator(targetClass, targetJarClassFile)
        if (targetJarClassFile.getName().endsWith('.jar')) {
            processor.insertInitCodeIntoJarFile(targetClass, targetJarClassFile)
        }
    }

    /**
     * 插入代码到jar文件中
     * @param jarFile the jar file which contains ModuleInitializer.class
     * @return
     */
    private File insertInitCodeIntoJarFile(String fileName, File jarFile) {
        if (jarFile) {
            def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
            if (optJar.exists())
                optJar.delete()
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = file.getInputStream(jarEntry)
                jarOutputStream.putNextEntry(zipEntry)
                if (fileName == entryName) {
                    def bytes = referHackWhenInit(inputStream)
                    jarOutputStream.write(bytes)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                inputStream.close()
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            file.close()

            if (jarFile.exists()) {
                jarFile.delete()
            }
            optJar.renameTo(jarFile)
        }
        return jarFile
    }

    private byte[] referHackWhenInit(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ClassVisitor cv = new InjectClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    class InjectClassVisitor extends ClassVisitor {
        InjectClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            //generate code into this method
            System.out.println("-----injectInitPlugin name-----${name}")
            if (name == "init") {
                return new AdviceAdapter(org.objectweb.asm.Opcodes.ASM5, cv.visitMethod(access, name, desc, signature, exceptions), access, name, desc) {
                    @Override
                    protected void onMethodEnter() {
                        System.out.println("-----injectInitPlugin-----")
                        List<String> plugins = KwPluginSingleInstance.instance.plugins
                        for (pluginName in plugins) {

                            System.out.println("-----inject---------------${pluginName}------------")
//                            mv.visitTypeInsn(NEW, pluginName)
//                            mv.visitInsn(DUP)
//                            mv.visitMethodInsn(INVOKESPECIAL, pluginName, "<init>", "()V", false)
//                            mv.visitMethodInsn(INVOKEVIRTUAL, pluginName, "initPlugin", "(Ljava/lang/String;)V", false)

                            mv.visitTypeInsn(NEW, pluginName);
                            mv.visitInsn(DUP);
//                            mv.visitVarInsn(ALOAD, 0);
                            mv.visitMethodInsn(INVOKESPECIAL, pluginName, "<init>", "()V", false);
                            mv.visitVarInsn(ALOAD, 1);
                            mv.visitMethodInsn(INVOKEVIRTUAL, pluginName, "initPlugin", "(Landroid/app/Application;)V", false);
                        }
                    }
                }
            }
            return super.visitMethod(access, name, desc, signature, exceptions)
        }
    }
}