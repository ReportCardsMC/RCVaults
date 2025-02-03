package xyz.reportcards.vaults.gui

import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.util.Slot
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.reportcards.vaults.VaultService
import xyz.reportcards.vaults.gui.utils.CustomChestGui
import xyz.reportcards.vaults.models.PlayerData
import xyz.reportcards.vaults.models.PlayerVault
import xyz.reportcards.vaults.models.VaultData
import xyz.reportcards.vaults.models.VaultItem
import xyz.reportcards.vaults.utils.not
import xyz.reportcards.vaults.utils.withMetaData

class VaultMenu(
    val player: Player,
    val vault: PlayerVault,
    val playerData: PlayerData
) {

    fun get(): CustomChestGui {
        var loaded = false
        val playerData = VaultService.instance.getPlayerData(player) ?: PlayerData(player.uniqueId, mutableListOf(), 0)
        val vaultData = playerData.vaults.find { it.id == vault.id } ?: VaultData(vault.id, "Vault ${vault.id}", 0)
        val gui = CustomChestGui(6, TextHolder.deserialize(vaultData.name))
        val contentPane = StaticPane(0,0,9,5)
        val bottomPane = StaticPane(0,5,9,1)
        bottomPane.addItem(GuiItem(ItemStack(Material.BARRIER).withMetaData {
            val meta = it.itemMeta
            meta.displayName(!"<red>Go Back")
            meta.lore(listOf(!"", !"<gray>Click to go back to the vaults menu"))
            meta
        }) {
            player.closeInventory()
            val newData = VaultService.instance.getPlayerData(player) ?: throw IllegalStateException("Player data is null")
            VaultMainMenu(player, newData).get().show(player)
        }, 4, 0)
        bottomPane.fillWith(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
        gui.addPane(contentPane)
        gui.addPane(bottomPane)

        bottomPane.setOnClick {
            it.isCancelled = true
        }

        gui.setOnClose {
            if (loaded) save(gui, contentPane)
        }

        gui.setOnShow {
            vault.contents.forEach {
                gui.inventory.setItem(it.slot, it.toItemStack())
            }
            loaded = true
        }
        return gui
    }

    private fun save(
        gui: ChestGui,
        contentPane: StaticPane
    ) {
        // Loop all slots in the content pane
        val width = contentPane.length
        val height = contentPane.height
        val guiItems = gui.inventory
        val vaultItems = mutableListOf<VaultItem>()
        var itemCount = 0
        for (y in 0..<height) {
            for (x in 0..<width) {
                val slot = guiItems.getItem(x + y * 9) ?: continue
                vaultItems.add(VaultItem.fromItemStack(x + y * 9, slot))
                itemCount += slot.amount
            }
        }
        //println("Vault items: ${vaultItems.size}")
        //println("Gui items: ${guiItems.contents.size}")

        val playerData = VaultService.instance.getPlayerData(player) ?: PlayerData(player.uniqueId, mutableListOf(), 0)
        val vaultData = playerData.vaults.find { it.id == vault.id } ?: VaultData(vault.id, "Vault ${vault.id}", 0)
        val newVault = PlayerVault(vault.id, vaultItems, width * height)
        VaultService.instance.saveVault(player, newVault)
        // Add vault to player data
        playerData.vaults.removeIf { it.id == vault.id }
        playerData.vaults.add(VaultData(newVault.id, vaultData.name, itemCount))
        VaultService.instance.setPlayerData(player, playerData)

        player.sendMessage("Saved vault ${vault.id}")
    }

}