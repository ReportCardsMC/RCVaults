package xyz.reportcards.vaults.utils

import org.bukkit.entity.Player

fun Player.getVaultsFromPermissions(): Int {
    val permissions = this.effectivePermissions
    // Vault amount permission: "vaults.amount.<amount>"
    val vaultPermissions = permissions.filter { it.permission.startsWith("vault.amount.") }
    if (vaultPermissions.isNotEmpty()) {
        // get highest amount
        val highestVaultPermission = vaultPermissions.maxBy { it.permission.substringAfter("vault.amount.").toInt() }
        return highestVaultPermission?.permission?.substringAfter("vault.amount.")?.toInt() ?: 0
    }
    return 0
}