package kidswant.architecture.plugin

class KwSettingExtension {

    /**
     * 本地库地址
     */
    String settingLibraryPath

    /**
     * 临时库名称
     */
    String baseLibName

    int targetSdkVersion = 29

    int minSdkVersion = 17

    int compileSdkVersion = 29

    String getSettingLibraryPath() {
        return settingLibraryPath
    }

    void setSettingLibraryPath(String settingLibraryPath) {
        this.settingLibraryPath = settingLibraryPath
    }

    String getBaseLibName() {
        return baseLibName
    }

    void setBaseLibName(String baseLibName) {
        this.baseLibName = baseLibName
    }

    int getTargetSdkVersion() {
        return targetSdkVersion
    }

    void setTargetSdkVersion(int targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion
    }

    int getMinSdkVersion() {
        return minSdkVersion
    }

    void setMinSdkVersion(int minSdkVersion) {
        this.minSdkVersion = minSdkVersion
    }

    int getCompileSdkVersion() {
        return compileSdkVersion
    }

    void setCompileSdkVersion(int compileSdkVersion) {
        this.compileSdkVersion = compileSdkVersion
    }
}
