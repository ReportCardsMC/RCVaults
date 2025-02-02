package xyz.reportcards.vaults

import co.aikar.commands.PaperCommandManager
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import xyz.reportcards.vaults.commands.VaultCommand
import xyz.reportcards.vaults.commands.VaultTestCommand
import xyz.reportcards.vaults.models.PlayerData
import xyz.reportcards.vaults.models.PlayerVault
import xyz.reportcards.vaults.utils.datastore.implementations.CompressedFileDatastore
import xyz.reportcards.vaults.utils.datastore.implementations.FileDatastore
import java.io.File

class RCVaults : JavaPlugin() {

    companion object {
        val instance: RCVaults by lazy { getPlugin(RCVaults::class.java) }
        val vaultService: VaultService by lazy {
            val service = Bukkit.getServicesManager().getRegistration(VaultService::class.java)
                ?: throw IllegalStateException("VaultService is not registered")

            service.provider
        }
    }

    private lateinit var commandManager: PaperCommandManager

    override fun onEnable() {
        commandManager = PaperCommandManager(this)
        // Plugin startup logic
        commandManager.registerCommand(VaultTestCommand())
        commandManager.registerCommand(VaultCommand())

        Bukkit.getServicesManager().register(VaultService::class.java, VaultService(
            CompressedFileDatastore(File(dataFolder, "vaults"), PlayerVault::class.java, ".json"),
            FileDatastore(File(dataFolder, "players"), PlayerData::class.java, "-compressed.json")
        ), this, ServicePriority.Normal)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
