package xyz.reportcards.vaults.utils

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun ItemStack.invisibleName(): ItemStack {
    return this.withMetaData {
        val meta = it.itemMeta
        meta.displayName(Component.text(""))
        meta
    }
}

fun ItemStack.withMetaData(modifier: (ItemStack) -> ItemMeta): ItemStack {
    this.itemMeta = modifier(this)
    return this
}