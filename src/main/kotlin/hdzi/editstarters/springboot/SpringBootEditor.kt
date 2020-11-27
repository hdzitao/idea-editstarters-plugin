package hdzi.editstarters.springboot

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.ThrowableComputable
import hdzi.editstarters.EditStarters
import hdzi.editstarters.buildsystem.ProjectDependency
import hdzi.editstarters.springboot.initializr.SpringInitializr
import hdzi.editstarters.ui.EditStartersDialog
import hdzi.editstarters.ui.InitializrUrlDialog
import hdzi.editstarters.ui.ShowErrorException

/**
 * 编辑器
 *
 * Created by taojinhou on 2019/1/11.
 */
abstract class SpringBootEditor(
    val context: DataContext,
    editStartersGetter: () -> EditStarters,
    dependenciesGetter: () -> List<ProjectDependency>
) : EditStarters by editStartersGetter() {

    private val existsDependencyDB: Map<String, ProjectDependency> =
        dependenciesGetter().associateBy({ it.point }, { it })

    private val springbootDependency =
        existsDependencyDB[ProjectDependency("org.springframework.boot", "spring-boot").point]

    /**
     * 启动编辑器
     */
    fun edit() {
        if (isSpringBootProject) {
            // 弹出spring initializr地址输入框
            val dialog = InitializrUrlDialog().show()
            // 检查url确定是否点击了ok
            if (dialog.isOK) {
                initSpringInitializr(dialog.url!!)
                EditStartersDialog(this).show()
            }
        } else throw ShowErrorException("Not a Spring Boot Project!")
    }

    var springInitializr: SpringInitializr? = null

    /**
     * 判断是否是spring boot项目
     */
    private val isSpringBootProject: Boolean = springbootDependency != null

    /**
     * 初始化Initializr
     */
    private fun initSpringInitializr(url: String) {
        val progressManager = ProgressManager.getInstance()
        progressManager.runProcessWithProgressSynchronously(ThrowableComputable<Unit, Exception> {
            progressManager.progressIndicator.isIndeterminate = true
            springInitializr = SpringInitializr(url, springbootDependency!!.version!!)
            existsDependencyDB.values.forEach { dep ->
                this.springInitializr!!.addExistsStarter(dep)
            }
        }, "Loading $url", false, context.getData(CommonDataKeys.PROJECT))
    }
}