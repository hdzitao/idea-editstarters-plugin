package io.github.hdzitao.editstarters.cache

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.OptionTag
import io.github.hdzitao.editstarters.springboot.SpringBoot

/**
 * 缓存
 *
 * @version 3.2.0
 */
@Service(Service.Level.PROJECT)
@State(
    name = "editstarters-initializr-cache",
    storages = [Storage(value = "editstarters/initializr-cache.xml", roamingType = RoamingType.DISABLED)]
)
class InitializrCache : PersistentStateComponent<InitializrCache.State?> {
    /**
     * 缓存bean
     */
    class State {
        @OptionTag(converter = SpringBootConverter::class)
        var springBoot: SpringBoot? = null
        var url: String? = null
        var oHub: String? = null
        var version: String? = null
        var updateTime: Long = 0
    }

    class SpringBootConverter : JSONConverter<SpringBoot>()

    private var state: State? = null

    /**
     * 获取缓存
     */
    fun getSpringBoot(url: String, version: String): SpringBoot? =
        if (state?.url == url && version == state?.version && state?.springBoot != null) {
            state?.springBoot
        } else {
            null
        }

    /**
     * 更新缓存
     */
    fun putSpringBoot(url: String, version: String, project: SpringBoot) {
        state?.url = url
        state?.version = version
        state?.springBoot = project
        state?.updateTime = System.currentTimeMillis()
    }

    val url: String?
        get() = state?.url

    val updateTime: Long?
        get() = state?.updateTime


    val oHubName: String?
        get() = state?.oHub


    /**
     * 缓存oHub
     */
    fun putOHubName(oHubName: String?) {
        state!!.oHub = oHubName
    }

    override fun getState(): State? {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): InitializrCache {
            return project.getService(InitializrCache::class.java)
        }
    }
}
