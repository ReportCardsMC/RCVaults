package xyz.reportcards.vaults.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import com.google.gson.Gson
import org.bukkit.entity.Player
import xyz.reportcards.vaults.models.PlayerVault
import xyz.reportcards.vaults.models.VaultItem
import kotlin.system.measureTimeMillis

@CommandAlias("vaulttest")
class VaultTestCommand: BaseCommand() {

    @Default
    fun onDefault(sender: Player) {
//        sender.sendMessage("Compressing item in hand:")
//        val item = sender.inventory.itemInMainHand
//        val nbt = NBT.itemStackToNBT(item)
//        sender.sendMessage("Original NBT: ${nbt.toString()}")
//        val decompressed = nbt.toString().toByteArray()
//        var compressed: ByteArray
//        val ms = measureTimeMillis {
//            compressed = CompressionUtils.compress(nbt.toString().toByteArray())
//        }
//        sender.sendMessage("Compression took $ms ms: %.2f MB -> %.2f MB".format(decompressed.size.toDouble() / 1024 / 1024, compressed.size.toDouble() / 1024 / 1024))
//        val decompressMs = measureTimeMillis {
//            val decompressedNBT = CompressionUtils.decompress(compressed)
//            sender.sendMessage("Decompressed NBT: ${decompressedNBT.toString()}")
//            val decompressed = NBT.readNBT(decompressedNBT.inputStream())
//            sender.sendMessage("Matches: ${decompressed.toString() == nbt.toString()}")
//        }
//        sender.sendMessage("Decompression took $decompressMs ms")
//        val items = sender.inventory.contents.mapNotNull { if (it != null) VaultItem.fromItemStack(it) else null }
        val items = sender.inventory.contents.mapIndexed { index, itemStack -> if (itemStack != null) VaultItem.fromItemStack(index, itemStack) else null }.filterNotNull()
        val fakeVault = PlayerVault(0, "Test", items, 9)
        sender.sendMessage("Vault Json: ${Gson().toJson(fakeVault)}")
        val compressed: ByteArray
        val compressMs = measureTimeMillis {
            compressed = fakeVault.compressed()
        }
        sender.sendMessage("Compressed vault: ${compressed.toString(Charsets.UTF_8)}")
        val decompressed: PlayerVault
        val decompressMs = measureTimeMillis {
            decompressed = PlayerVault.fromCompressed(compressed)
        }
//        sender.sendMessage("Decompressed vault: ${Gson().toJson(decompressed)}")
        sender.sendMessage("Matches: ${decompressed.contents == fakeVault.contents}")

        // Run size comparisons now
        val sizeOriginal = Gson().toJson(fakeVault).toByteArray().size
        val sizeCompressed = compressed.size
//        sender.sendMessage("Original size: $sizeOriginal, compressed size: $sizeCompressed")
        // Convert to KB
        val sizeOriginalKb = sizeOriginal.toDouble() / 1024
        val sizeCompressedKb = sizeCompressed.toDouble() / 1024
        sender.sendMessage("Original size: %.2f KB, compressed size: %.2f KB".format(sizeOriginalKb, sizeCompressedKb))
        sender.sendMessage("Compression ratio: ${sizeCompressed.toDouble() / sizeOriginal.toDouble()}")

        // Time messages
        sender.sendMessage("Compression took %.2f ms".format(compressMs.toDouble() / 1000))
        sender.sendMessage("Decompression took %.2f ms".format(decompressMs.toDouble() / 1000))
    }

}