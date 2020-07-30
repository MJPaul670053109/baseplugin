package kidswant.architecture.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.initialization.Settings

/**
 * @author MJ on 2020-1-14
 */
class KwSettingPlugin implements Plugin<Settings> {

    Settings settings

    def targetSdkVersion = 29
    def minSdkVersion = 17
    def compileSdkVersion = 29


    @Override
    void apply(Settings settings) {
        this.settings = settings
        settings.getExtensions().create("settingConfig", KwSettingExtension)


        settings.ext.includeWithApi = this.&includeWithApi
    }


    def includeWithApi(List<String> moduleNames) {
        KwSettingExtension ks = settings.getExtensions().getByName("settingConfig")

        targetSdkVersion = ks.targetSdkVersion
        minSdkVersion = ks.minSdkVersion
        compileSdkVersion = ks.compileSdkVersion
        List<String> originDirs = new ArrayList<>()
        for (moduleName in moduleNames) {
            settings.include(moduleName)
            settings.project(moduleName).projectDir = new File(ks.settingLibraryPath + moduleName.split(":")[1])
            originDirs.add(settings.project(moduleName).projectDir)
        }

        def sdkName = ks.baseLibName == null ? "kwcommon" : ks.baseLibName

        def targetDir = ks.settingLibraryPath + sdkName
        settings.include ":$sdkName"
        settings.project(":$sdkName").projectDir = new File(targetDir)


        println("---------创建gradle文件 --------")
        createBuild(targetDir)


        settings.gradle.projectsLoaded {
            println("---------清空plugin-common库的java文件夹下的java和kt文件 --------")
            cleanFile(settings.gradle.rootProject, targetDir)

            println("---------开始复制.japi和.kapi文件到底层库--------")
            copyFile(settings.gradle.rootProject, originDirs, targetDir)

            createManifest(targetDir)

            println("---------将.japi和.kapi 文件重新命名--------")
            renameApiFiles(settings.gradle.rootProject, targetDir, '.japi', '.java')
            renameApiFiles(settings.gradle.rootProject, targetDir, '.kapi', '.kt')

            println("---------创建InitPlugin文件--------")
            createInitPlugin(targetDir)
            println("---------创建Bumblebee文件--------")
            createBumblebee(targetDir)
            println("---------创建IPlugin文件--------")
            createIPlugin(targetDir)
            println("---------创建ModulePlugin文件--------")
            createModulePlugin(targetDir)
        }

        settings.gradle.projectsLoaded {
            Project project = settings.gradle.rootProject
            project.subprojects.each() { module ->
                if (moduleNames.contains(":" + module.name)) {
                    module.afterEvaluate {
                        println module.name + "添加的依赖模块:$sdkName"
                        module.dependencies.add("api", module.getRootProject().project(":$sdkName"))
                    }
                }
            }
        }

    }

    static def cleanFile(Project project, String targetDir) {
        project.delete project.fileTree(targetDir + "/src/main/java/").matching {
            include '**/*.kt'
            include '**/*.java'
        }
    }

    static def copyFile(Project project, ArrayList<String> originDirs, String targetDir) {
        for (originDir in originDirs) {
            project.copy() {
                from originDir
                into targetDir
                include '**/*.japi'
                include '**/*.kapi'
            }
        }
    }


    static def createManifest(String targetDir) {

        File dir = new File(targetDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        def build = new File(targetDir + "/src/main/AndroidManifest.xml")

        build.createNewFile()

        build.write("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package=\"com.haiziwang.base\">\n" +
                "\n" +
                "    <application />\n" +
                "</manifest>")
    }


    //创建build.gradle文件
    def createBuild(String targetDir) {
        File dir = new File(targetDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        def build = new File(targetDir + "/build.gradle")

        build.createNewFile()
        build.write("apply plugin: 'com.android.library'\n" +
                "apply plugin: 'kotlin-android'\n" +
                "apply plugin: 'kotlin-android-extensions'\n" +
                "android {\n" +
                "    compileSdkVersion ${compileSdkVersion}\n" +
                "    buildToolsVersion \"29.0.3\"\n" +
                "\n" +
                "\n" +
                "    defaultConfig {\n" +
                "        minSdkVersion ${minSdkVersion}\n" +
                "        targetSdkVersion ${targetSdkVersion}\n" +
                "        versionCode 1\n" +
                "        versionName \"1.0\"\n" +
                "\n" +
                "        testInstrumentationRunner \"androidx.test.runner.AndroidJUnitRunner\"\n" +
                "        consumerProguardFiles 'consumer-rules.pro'\n" +
                "    }\n" +
                "\n" +
                "    buildTypes {\n" +
                "        release {\n" +
                "            minifyEnabled false\n" +
                "            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n" +
                "\n" +
                "dependencies {\n" +
                "    implementation fileTree(dir: 'libs', include: ['*.jar'])\n" +
                "    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.50'\n" +
                "    implementation 'androidx.appcompat:appcompat:1.1.0'\n" +
                "    implementation 'androidx.core:core-ktx:1.2.0'\n" +
                "}")

    }

    //创建InitPlugin.java文件
    static def createInitPlugin(String targetDir) {
        File dir = new File(targetDir + "/src/main/java/com/haiziwang/base")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        def build = new File(targetDir + "/src/main/java/com/haiziwang/base/InitPlugin.java")

        build.createNewFile()
        build.write("package com.haiziwang.base;\n" +
                "import android.app.Application;\n" +
                "\n" +
                "public class InitPlugin {\n" +
                "    public void init(Application s) {}\n" +
                "}")

    }

    //创建IPlugin.kt
    static def createIPlugin(String targetDir) {
        File dir = new File(targetDir + "/src/main/java/com/haiziwang/base")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        def build = new File(targetDir + "/src/main/java/com/haiziwang/base/IPlugin.kt")

        build.createNewFile()
        build.write("package com.haiziwang.base\n" +
                "\n" +
                "interface IPlugin")

    }

    //创建ModulePlugin.kt
    static def createModulePlugin(String targetDir) {
        File dir = new File(targetDir + "/src/main/java/com/haiziwang/base")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        def build = new File(targetDir + "/src/main/java/com/haiziwang/base/ModulePlugin.kt")

        build.createNewFile()
        build.write("package com.haiziwang.base\n" +
                "import android.app.Application\n" +
                "abstract class ModulePlugin : IPlugin {\n" +
                "    open fun initPlugin(s: Application) {}\n" +
                "}")

    }

    static def createBumblebee(String targetDir) {
        File dir = new File(targetDir + "/src/main/java/com/haiziwang/base")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        def build = new File(targetDir + "/src/main/java/com/haiziwang/base/KwPluginMap.kt")

        build.createNewFile()
        build.write("package com.haiziwang.base\n" +
                "\n" +
                "import java.util.concurrent.ConcurrentHashMap\n" +
                "\n" +
                "object KwPluginMap {\n" +
                "\n" +
                "    private val services = ConcurrentHashMap<Class<*>, Any>()\n" +
                "\n" +
                "    @JvmStatic\n" +
                "    fun register(service: Class<*>, serviceImp: Any) {\n" +
                "        services[service] = serviceImp\n" +
                "    }\n" +
                "\n" +
                "    @Suppress(\"UNCHECKED_CAST\")\n" +
                "    @JvmStatic\n" +
                "    fun <T> visit(service: Class<T>): T {\n" +
                "        if (services.containsKey(service)) {\n" +
                "            return (services[service] as T)\n" +
                "        }\n" +
                "        throw RuntimeException(\"serviceImp has not register yet.\")\n" +
                "    }\n" +
                "}")

    }

    /**
     * 将.api 结尾的文件全部替换掉
     */
    static def renameApiFiles(Project project, String root_dir, String suffix, String replace) {
        FileTree files = project.fileTree(root_dir).include("**/*$suffix")
        files.each { File file ->
            file.renameTo(new File(file.absolutePath.replace(suffix, replace)))
            file.delete()
        }
    }
}