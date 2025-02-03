package xyz.reportcards.vaults.gui.utils

import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import org.bukkit.entity.HumanEntity
import org.bukkit.plugin.Plugin
import xyz.reportcards.vaults.RCVaults

class CustomChestGui(
    rows: Int,
    title: TextHolder,
    plugin: Plugin
): ChestGui(rows, title, plugin) {

    private var onShow: ((HumanEntity) -> Unit)? = null

    constructor(
        rows: Int,
        title: TextHolder
    ): this(rows, title, RCVaults.instance)

    override fun show(humanEntity: HumanEntity) {
        super.show(humanEntity)
        onShow?.invoke(humanEntity)
    }

    fun setOnShow(onShow: ((HumanEntity) -> Unit)?) {
        this.onShow = onShow
    }

}