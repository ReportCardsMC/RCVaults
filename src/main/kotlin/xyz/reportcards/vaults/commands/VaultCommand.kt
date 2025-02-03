package xyz.reportcards.vaults.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player
import xyz.reportcards.vaults.VaultService
import xyz.reportcards.vaults.gui.VaultMainMenu
import xyz.reportcards.vaults.gui.VaultMenu
import xyz.reportcards.vaults.models.PlayerData
import xyz.reportcards.vaults.models.PlayerVault
import xyz.reportcards.vaults.utils.getVaultsFromPermissions
import xyz.reportcards.vaults.utils.not

@CommandAlias("vault")
class VaultCommand: BaseCommand() {

    @Default
    fun onDefault(sender: Player, @Default("0") @Conditions("limits:min=0,max=30") vault: Int) {

        val playerData = VaultService.instance.getPlayerData(sender) ?: PlayerData(sender.uniqueId, mutableListOf(), 0)
        val playerVaults = sender.getVaultsFromPermissions()
        val hasVaults = playerVaults > 0 || playerData.extraVaults > 0 || playerData.vaults.size > 0

        if (!hasVaults) {
            sender.sendMessage(!"<red>You don't have any vaults")
            return
        }

        if (vault > 0) {
            val playerVault = VaultService.instance.getVault(sender, vault) ?: PlayerVault(vault, mutableListOf(), 9*5)
            if (vault <= playerVaults) {
                VaultMenu(sender, playerVault, playerData).get().show(sender)
            } else {
                sender.sendMessage(!"<red>You can't access vault $vault")
            }
        } else {
            val gui = VaultMainMenu(sender, playerData).get()
            gui.show(sender)
        }
    }

}