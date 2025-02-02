package xyz.reportcards.vaults.models

import com.google.gson.GsonBuilder
import xyz.reportcards.vaults.utils.CompressionUtils


private val GSON = GsonBuilder().create()
class PlayerVault(
    val id: Int,
    val name: String,
    val contents: List<VaultItem>,
    val size: Int = 9
) {

    companion object {
        fun fromCompressed(compressed: ByteArray): PlayerVault {
            val json = CompressionUtils.decompress(compressed).toString(Charsets.UTF_8)
            return GSON.fromJson(json, PlayerVault::class.java)
        }
    }

    fun compressed(): ByteArray {
        val json = GSON.toJson(this)
        return CompressionUtils.compress(json.toByteArray())
    }

}