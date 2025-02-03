package xyz.reportcards.vaults.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import org.bukkit.entity.Player
import xyz.reportcards.vaults.VaultService
import xyz.reportcards.vaults.gui.VaultMainMenu
import xyz.reportcards.vaults.models.PlayerData

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
        } else {
            val playerData = VaultService.instance.getPlayerData(sender) ?: PlayerData(sender.uniqueId, mutableListOf(), 0)
            val gui = VaultMainMenu(sender, playerData).get()
            gui.show(sender)
        }
    }

}