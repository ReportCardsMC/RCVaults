package xyz.reportcards.vaults

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.reportcards.vaults.models.PlayerData
import xyz.reportcards.vaults.models.PlayerVault
import xyz.reportcards.vaults.models.VaultData
import xyz.reportcards.vaults.utils.datastore.Datastore

class VaultService(
    private val vaultDatastore: Datastore<PlayerVault>,
    private val playerDatastore: Datastore<PlayerData>
) {

    companion object {
        val instance: VaultService by lazy { Bukkit.getServicesManager().load(VaultService::class.java)!! }
    }

    fun saveVault(player: Player, vault: PlayerVault): Boolean {
        return vaultDatastore.set(player.uniqueId.toString() + "-" + vault.id, vault)
    }

    fun getVault(player: Player, id: Int): PlayerVault? {
        return vaultDatastore.get(player.uniqueId.toString() + "-" + id)
    }

    fun deleteVault(player: Player, id: Int): Boolean {
        return vaultDatastore.delete(player.uniqueId.toString() + "-" + id)
    }

    fun getVaults(player: Player): List<VaultData> {
        return playerDatastore.get(player.uniqueId.toString())?.vaults ?: emptyList()
    }

    fun setPlayerData(player: Player, data: PlayerData): Boolean {
        return playerDatastore.set(player.uniqueId.toString(), data)
    }

    fun getPlayerData(player: Player): PlayerData? {
        return playerDatastore.get(player.uniqueId.toString())
    }

    fun deletePlayerData(player: Player): Boolean {
        return playerDatastore.delete(player.uniqueId.toString())
    }

}