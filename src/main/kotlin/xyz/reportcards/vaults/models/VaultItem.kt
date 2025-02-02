package xyz.reportcards.vaults.models

import de.tr7zw.nbtapi.NBT
import org.bukkit.inventory.ItemStack
import xyz.reportcards.vaults.utils.CompressionUtils

data class VaultItem(
    val slot: Int,
    val data: String
) {
    companion object {
        fun fromItemStack(slot: Int, itemStack: ItemStack): VaultItem {
            val nbt = NBT.itemStackToNBT(itemStack);
            return VaultItem(slot, nbt.toString())
        }
    }

    fun toItemStack(): ItemStack {
//        val decompressed = CompressionUtils.decompress(data.toByteArray())
        val readNbt = NBT.parseNBT(data)
        return NBT.itemStackFromNBT(readNbt)!!
    }
}
