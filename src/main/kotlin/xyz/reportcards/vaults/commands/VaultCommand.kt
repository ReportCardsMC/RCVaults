package xyz.reportcards.vaults.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import org.bukkit.entity.Player
import xyz.reportcards.vaults.VaultService
import xyz.reportcards.vaults.models.PlayerData
import xyz.reportcards.vaults.models.PlayerVault
import xyz.reportcards.vaults.models.VaultData
import xyz.reportcards.vaults.models.VaultItem

@CommandAlias("vault")
class VaultCommand: BaseCommand() {

    @Default
    fun onDefault(sender: Player, @Optional vault: Int?) {
        sender.sendMessage("Opening vault gui: $vault")
        sender.sendMessage("This will open the main vault gui, with the optional vault id")

        if (vault != null) {
            val playerVault = VaultService.instance.getVault(sender, vault)
            if (playerVault != null) {
                sender.sendMessage("Opening vault with id $vault")
            } else {
                sender.sendMessage("Vault with id $vault does not exist")
            }
        }
    }

    @CommandPermission("vault.save")
    @CommandAlias("save")
    fun onSave(sender: Player, vault: Int) {
        sender.sendMessage("Saving vault $vault")
        val playerData = VaultService.instance.getPlayerData(sender) ?: PlayerData(sender.uniqueId, mutableListOf(), 9)
        if (vault > playerData.extraVaults) {
            sender.sendMessage("You do not have that many vaults")
            return
        }

        val vaultData = VaultData(vault, "Vault $vault")
        // add or replace existing vault
        playerData.vaults.find { it.id == vault }?.let { playerData.vaults.remove(it) }
        playerData.vaults.add(vaultData)
        VaultService.instance.setPlayerData(sender, playerData)
        val vaultItems = sender.inventory.contents.mapIndexed { index, itemStack -> if (itemStack != null) VaultItem.fromItemStack(index, itemStack) else null }.filterNotNull()
        VaultService.instance.saveVault(sender, PlayerVault(vault, "Vault $vault", vaultItems, 9*4))
        sender.sendMessage("Saved vault $vault")
    }

    @CommandPermission("vault.load")
    @CommandAlias("load")
    fun onLoad(sender: Player, vault: Int) {
        sender.sendMessage("Loading vault $vault")
        val playerData = VaultService.instance.getPlayerData(sender) ?: PlayerData(sender.uniqueId, mutableListOf(), 9)
        val vaultData = playerData.vaults.find { it.id == vault } ?: throw IllegalArgumentException("Vault $vault does not exist")
        val playerVault = VaultService.instance.getVault(sender, vault) ?: throw IllegalArgumentException("Vault $vault does not exist")
        val vaultItems = playerVault.contents.map { it.toItemStack() }
        sender.inventory.clear()
        playerVault.contents.forEach { vaultItem ->
            sender.inventory.setItem(vaultItem.slot, vaultItem.toItemStack())
        }
        sender.sendMessage("Loaded vault $vault")
    }

}