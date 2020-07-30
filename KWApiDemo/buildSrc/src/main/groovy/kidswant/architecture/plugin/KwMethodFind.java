package kidswant.architecture.plugin;


import org.objectweb.asm.ClassVisitor;

import java.io.File;

/**
 * Created by MJ on 2020-1-014
 * 寻找要初始化的类以及需要注入代码的类
 */
public class KwMethodFind extends ClassVisitor {

    private File dest;

    KwMethodFind(int i, ClassVisitor classVisitor, File dest) {
        super(i, classVisitor);
        this.dest = dest;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if (name.endsWith("InitPlugin")) {
            KwPluginSingleInstance.getInstance().setTargetClass(name + ".class");
            KwPluginSingleInstance.getInstance().setTargetClassJarFile(dest);
            System.out.println("-----inject---------------" + name);
        }

        System.out.println("---------name-----------" + name + "-----------superName" + superName);
        if (superName.endsWith("ModulePlugin")) {
            KwPluginSingleInstance.getInstance().getPlugins().add(name);
        }


    }
}
