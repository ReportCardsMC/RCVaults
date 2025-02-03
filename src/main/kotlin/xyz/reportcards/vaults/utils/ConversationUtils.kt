package xyz.reportcards.vaults.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.RegexPrompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import xyz.reportcards.vaults.VaultService
import xyz.reportcards.vaults.models.PlayerData
import xyz.reportcards.vaults.models.VaultData

class ConversationUtils {

// Vault name regex should allow all letters, numbers, and spaces with a minimum length of 3 and a maximum length of 16
    class VaultPrompt(
        val player: Player,
        val vault: VaultData,
        val playerData: PlayerData
    ): RegexPrompt("([a-zA-Z0-9 ]{3,16})") {
        override fun getPromptText(context: ConversationContext): String {
            return ChatColor.translateAlternateColorCodes('&', "&7Enter the new name for vault ${vault.id} (3-16 Characters)\n&cType 'cancel' to cancel")
        }

        override fun acceptValidatedInput(context: ConversationContext, input: String): Prompt? {
            if (input.equals("cancel", true)) {
                player.sendMessage("<red>Cancelled changing vault name")
                return Prompt.END_OF_CONVERSATION
            }
            val newVault = VaultData(vault.id, input, vault.itemCount)
            playerData.vaults.removeIf { it.id == vault.id }
            playerData.vaults.add(newVault)
            VaultService.instance.setPlayerData(player, playerData)
            player.sendMessage(!"<green>Vault name changed to <reset>${newVault.name}")
            return Prompt.END_OF_CONVERSATION
        }

    override fun getFailedValidationText(context: ConversationContext, invalidInput: String): String {
        return ChatColor.translateAlternateColorCodes('&', "&cInvalid vault name - must be 3-16 characters")
    }

    }

}