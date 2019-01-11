package hdzi.editstarters.springboot

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.progress.ProgressManager
import hdzi.editstarters.springboot.bean.StarterInfo
import hdzi.editstarters.ui.EditStartersDialog
import hdzi.editstarters.ui.InitializrUrlDialog

/**
 * 编辑器
 *
 * Created by taojinhou on 2019/1/11.
 */
abstract class SpringBootEditor(val context: DataContext) {
    /**
     * 启动编辑器
     */
    fun edit() {
        if (isSpringBootProject()) {
            // 弹出spring initializr地址输入框
            val dialog = InitializrUrlDialog().show()
            // 检查url确定是否点击了ok && initializr是否初始化成功
            if (dialog.isOK && initSpringInitializr(dialog.url!!)) {
                EditStartersDialog(this).show()
            }
        }
    }

    var springInitializr: SpringInitializr? = null

    abstract val version: String?

    /**
     * 判断是否是spring boot项目
     */
    abstract fun isSpringBootProject(): Boolean

    /**
     * 初始化Initializr
     */
    fun initSpringInitializr(url: String): Boolean =
        ProgressManager.getInstance().runProcessWithProgressSynchronously({
            springInitializr = SpringInitializr(url, version!!)
            addExistsStarters()
        }, "Load ${url}", true, context.getData(DataKeys.PROJECT))


    /**
     * 标记已存在的starters
     */
    abstract fun addExistsStarters()

    abstract fun addDependencies(starterInfos: Collection<StarterInfo>)

    abstract fun removeDependencies(starterInfos: Collection<StarterInfo>)
}