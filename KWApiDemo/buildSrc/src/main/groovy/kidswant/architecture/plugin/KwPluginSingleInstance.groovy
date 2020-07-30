/*
 * Copyright 2018 qiugang(thisisqg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kidswant.architecture.plugin


/**
 * @author MJ on 2020-1-14
 */
class KwPluginSingleInstance {

    static KwPluginSingleInstance getInstance() {
        return HOLDER.instance
    }

    private List<String> plugins = new ArrayList<>()

    private String targetClass

    private File targetClassJarFile

    File getTargetClassJarFile() {
        return targetClassJarFile
    }

    void setTargetClassJarFile(File targetClassJarFile) {
        this.targetClassJarFile = targetClassJarFile
    }

    String getTargetClass() {
        return targetClass
    }

    void setTargetClass(String targetClass) {
        this.targetClass = targetClass
    }

    private static class HOLDER {
        private static final KwPluginSingleInstance instance = new KwPluginSingleInstance()
    }

    List<String> getPlugins() {
        return plugins
    }

    void setPlugins(List<String> plugins) {
        this.plugins = plugins
    }
}
