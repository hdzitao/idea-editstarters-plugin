package hdzi.editstarters.springboot

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.ThrowableComputable
import hdzi.editstarters.EditStarters
import hdzi.editstarters.bean.project.ProjectDependency
import hdzi.editstarters.ui.EditStartersDialog
import hdzi.editstarters.ui.InitializrUrlDialog

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
        } else throw Exception("Not a Spring Boot Project!")
    }

    var springInitializr: SpringInitializr? = null

    val currentVersion: String? = springbootDependency?.version

    /**
     * 判断是否是spring boot项目
     */
    private val isSpringBootProject: Boolean = springbootDependency != null

    /**
     * 初始化Initializr
     */
    private fun initSpringInitializr(url: String) =
        ProgressManager.getInstance().runProcessWithProgressSynchronously(ThrowableComputable<Unit, Exception> {
            springInitializr = SpringInitializr(url, currentVersion!!)
            existsDependencyDB.values.forEach { dep ->
                this.springInitializr!!.addExistsStarter(dep)
            }
        }, "Load ${url}", false, context.getData(DataKeys.PROJECT))
}