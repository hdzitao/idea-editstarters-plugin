package hdzi.editstarters.ui

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.CollectionListModel
import hdzi.editstarters.bean.StarterInfo
import hdzi.editstarters.springboot.SpringBootEditor
import org.apache.commons.lang.WordUtils
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*

class EditStartersDialog(springBoot: SpringBootEditor) {
    private lateinit var root: JPanel
    private lateinit var buttonOK: JButton
    private lateinit var buttonCancel: JButton
    private lateinit var versionComboBox: JComboBox<String>
    private lateinit var moduleList: JList<String>
    private lateinit var starterList: JList<StarterInfo>
    private lateinit var selectList: JList<StarterInfo>
    private lateinit var searchField: JTextField
    private val frame: JFrame
    private val title = "Edit Starters"
    private val addStarters = HashSet<StarterInfo>(64)
    private val removeStarters = HashSet<StarterInfo>(64)
    private val toolTipTextCache = WeakHashMap<StarterInfo, String>() // 加个缓存

    init {
        val initializr = springBoot.springInitializr!!

        this.frame = JFrame(this.title)
        this.frame.contentPane = this.root

        // boot版本选框
        this.versionComboBox.model = CollectionComboBoxModel(
            initializr.version.values!!.map { it.id },
            initializr.currentVersionID
        )
        this.versionComboBox.isEnabled = false

        // 取消按钮
        this.buttonCancel.addActionListener { this.frame.dispose() }

        // ok按钮
        this.buttonOK.addActionListener {
            WriteCommandAction.runWriteCommandAction(springBoot.context.getData<Project>(CommonDataKeys.PROJECT)) {
                springBoot.addStarters(this.addStarters)
                springBoot.removeStarters(this.removeStarters)
            }
            this.frame.dispose()
        }

        val modulesMap = initializr.modulesMap

        // Module列表
        this.moduleList.model = CollectionListModel(modulesMap.keys)
        this.moduleList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                searchField.text = ""
                val name = moduleList.selectedValue
                starterList.model = CollectionListModel(modulesMap[name]!!)
            }
        })

        // 显示详细信息
        val showDescAdapter = object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                @Suppress("unchecked_cast")
                val list = e.source as JList<StarterInfo>
                val index = list.locationToIndex(e.point)
                if (index > -1) {
                    val starter = list.model.getElementAt(index)
                    list.toolTipText = starter.getStarterInfoToolTipText()
                }
            }
        }

        // Starter列表
        this.starterList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) { // 按两下选择
                    val starterInfo = starterList.selectedValue
                    if (!starterInfo.canBeAdded) return  // 检查一下是否允许添加
                    if (starterInfo.exist) { // 对于已存在的starter，添加就是从删除列表里删除
                        removeStarters.remove(starterInfo)
                    } else { // 对于不存在的starter，添加直接加入添加列表
                        addStarters.add(starterInfo)
                    }
                    // 去重显示
                    val listModel = selectList.model as CollectionListModel<StarterInfo>
                    if (!listModel.contains(starterInfo)) {
                        listModel.add(starterInfo)
                    }
                }
            }
        })
        this.starterList.addMouseMotionListener(showDescAdapter)

        // selected列表
        this.selectList.model = CollectionListModel(initializr.existStarters)
        this.selectList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) { // 按两下删除
                    val starterInfo = selectList.selectedValue
                    if (starterInfo.exist) { // 对于已存在的starter，删除就是加入删除列表
                        removeStarters.add(starterInfo)
                    } else { // 对于不存在的starter，删除是从添加列表里删除
                        addStarters.remove(starterInfo)
                    }
                    // 显示
                    (selectList.model as CollectionListModel<StarterInfo>).remove(starterInfo)
                }
            }
        })
        this.selectList.addMouseMotionListener(showDescAdapter)

        // 搜索框
        this.searchField.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                moduleList.clearSelection()
                val searchKey = searchField.text.toLowerCase()
                val result = initializr.searchDB.asSequence()
                    .filter { it.key.contains(searchKey) }
                    .map { it.value }
                    .toList()

                starterList.model = CollectionComboBoxModel(result)
            }
        })
    }

    fun show() {
        this.frame.pack()
        this.frame.setLocationRelativeTo(null) // 中间显示
        this.frame.isVisible = true
    }

    private fun StarterInfo.getStarterInfoToolTipText(): String =
        toolTipTextCache.computeIfAbsent(this) { info ->
            val buffer = StringBuilder()
            if (info.groupId != null) {
                buffer.append("GroupId: ").append(info.groupId).append("<br/>")
                    .append("ArtifactId: ").append(info.artifactId).append("<br/>")
                    .append("Scope: ").append(info.scope).append("<br/>")
                if (info.version != null) {
                    buffer.append("Version: ").append(info.version).append("<br/>")
                }
            } else if (info.versionRange != null) {
                buffer.append("VersionRange: ").append(info.versionRange).append("<br/>")
            }

            buffer.append("<br/>").append(WordUtils.wrap(info.description, 50, "<br/>", false))

            buffer.toString()
        }
}
