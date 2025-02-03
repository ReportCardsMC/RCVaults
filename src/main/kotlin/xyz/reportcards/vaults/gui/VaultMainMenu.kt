package xyz.reportcards.vaults.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.reportcards.vaults.RCVaults
import xyz.reportcards.vaults.VaultService
import xyz.reportcards.vaults.models.PlayerData
import xyz.reportcards.vaults.models.PlayerVault
import xyz.reportcards.vaults.models.VaultData
import xyz.reportcards.vaults.utils.*

class VaultMainMenu(
    val player: Player,
    val playerData: PlayerData
) {
    
    fun get(): ChestGui {
        val vaultCount = playerData.extraVaults + player.getVaultsFromPermissions()
        val vaultSlots: MutableMap<Int, Boolean> = playerData.vaults.associate { it.id to true }.toMutableMap()
        for (i in 1..vaultCount) {
            if (vaultSlots[i] == null) {
                vaultSlots[i] = false // False because the vault hasn't opened yet
            }
        }
        val rows = 2 + (vaultSlots.size/9)
        val gui = ChestGui(rows, "Player Vaults")
        val topPane = StaticPane(0, 0, 9, 1, Pane.Priority.HIGHEST)
        topPane.fillWith(ItemStack(Material.BLACK_STAINED_GLASS_PANE).invisibleName())
        gui.addPane(topPane)
        val vaultPane = StaticPane(0, 1, 9, rows-1, Pane.Priority.HIGHEST)

        for ((i, slot) in vaultSlots.keys.sortedBy { it }.withIndex()) {
            val isCreated = vaultSlots[slot] ?: false
            val vaultItem = GuiItem(ItemStack(Material.CHEST).withMetaData { itemStack ->
                val vaultData = playerData.vaults.find { it.id == slot } ?: VaultData(slot, "Vault $slot", 0)
                val meta = itemStack.itemMeta
                meta.displayName(!"<yellow>${vaultData.name}")
                if (isCreated) {
                    meta.lore(listOf(!"", !"<gray>Total Items:<white> ${vaultData.itemCount.commas()}", !"<gray>Left click to open", !"<gray>Right click to edit name", !"", !"<dark_gray>(Vault $slot)"))
                } else {
                    meta.lore(listOf(!"", !"<gray>Click to create"))
                }
                meta
            }) { event ->
                if (isCreated && event.isRightClick) {
                    player.closeInventory()
                    val vaultData = playerData.vaults.find { it.id == slot } ?: VaultData(slot, "Vault $slot", 0)
                    val prompt = ConversationUtils.VaultPrompt(player, vaultData, playerData)
                    val conversation = RCVaults.instance.conversationFactory.withFirstPrompt(prompt).withTimeout(30).withLocalEcho(false).buildConversation(player)
                    conversation.begin()
                    return@GuiItem
                }
                player.sendMessage("Opening vault $slot")
                val playerVault = VaultService.instance.getVault(player, slot) ?: PlayerVault(
                    slot,
                    mutableListOf(),
                    0
                )
                VaultMenu(player, playerVault, playerData).get().show(player)
            }

            vaultPane.addItem(vaultItem, i % 9, i / 9)
        }
        gui.addPane(vaultPane)
        gui.setOnTopClick { it.isCancelled = true }
        return gui
    }
    
}