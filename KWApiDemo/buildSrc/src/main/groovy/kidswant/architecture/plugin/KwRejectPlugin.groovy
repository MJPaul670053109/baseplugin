package kidswant.architecture.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author MJ on 2020-1-14
 */
class KwRejectPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("--------startPlugin--------")
        project.extensions.findByType(BaseExtension.class).registerTransform(new KwAppTransForm())
    }
}