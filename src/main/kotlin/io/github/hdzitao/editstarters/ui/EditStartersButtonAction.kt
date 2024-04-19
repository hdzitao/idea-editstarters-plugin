package io.github.hdzitao.editstarters.ui

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.ThrowableComputable
import io.github.hdzitao.editstarters.buildsystem.BuildSystem
import io.github.hdzitao.editstarters.cache.InitializrCache
import io.github.hdzitao.editstarters.initializr.*
import io.github.hdzitao.editstarters.ohub.GitHub
import io.github.hdzitao.editstarters.ohub.Gitee
import io.github.hdzitao.editstarters.version.Versions

/**
 * Edit Starter按钮
 *
 * @version 3.2.0
 */
abstract class EditStartersButtonAction : AnAction() {
    private val initializrs = arrayOf(
        CacheInitializr(),
        StartSpringInitializr(),
        OHubInitializr(),
    )

    private val oHubs = arrayOf(
        GitHub(),
        Gitee(),
    )

    override fun actionPerformed(e: AnActionEvent) {
        try {
            // 构建系统
            val buildSystem = newBuildSystem(e.dataContext)
            // 检查Spring boot
            if (!buildSystem.springBootProject) {
                throw ShowErrorException("Not a Spring Boot Project!")
            }
            // 缓存
            val project = e.project ?: throw ShowErrorException.internal()
            val initializrCache = InitializrCache.getInstance(project)
            // 初始化
            initializrCache.initialize()
            // spring boot version
            val version = Versions.parse(buildSystem.springbootDependency!!.version!!)
            // 弹出spring initializr地址输入框
            val initializrDialog = InitializrDialog(initializrCache, version, oHubs)
            initializrDialog.showDialog()
            // 获取url
            val url = initializrDialog.url
            if (url.isNullOrBlank()) {
                return
            }
            // 组装参数
            val request = InitializrRequest(
                project,
                buildSystem,
                url,
                version,
                initializrDialog.isEnableCache,
                initializrDialog.oHub
            )
            // 组装返回
            val response = InitializrResponse()
            // 执行
            val progressManager = ProgressManager.getInstance()
            progressManager.runProcessWithProgressSynchronously(ThrowableComputable<Void, Exception> {
                progressManager.progressIndicator.isIndeterminate = true
                InitializrChain(initializrs).initialize(request, response)
                null
            }, "Loading $url", true, project)
            // 模块弹窗
            EditStartersDialog(request, response).show()
        } catch (throwable: Throwable) {
            // 所有异常弹错误框
            val message = if (throwable is ShowErrorException) {
                throwable.message
            } else {
                throwable.javaClass.simpleName + ": " + throwable.message
            }

            Messages.showErrorDialog(message, "Edit Starters Error")
        }
    }

    /**
     * 判断按钮是否显示动作
     */
    override fun update(e: AnActionEvent) {
        val data = e.getData(CommonDataKeys.PSI_FILE)
        if (data != null) {
            val name = data.name
            e.presentation.isEnabled = isMatched(name)
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    /**
     * 判断文件名是否符合构建工具的要求
     */
    protected abstract fun isMatched(name: String): Boolean

    /**
     * new一个BuildSystem
     */
    protected abstract fun newBuildSystem(dataContext: DataContext): BuildSystem
}
