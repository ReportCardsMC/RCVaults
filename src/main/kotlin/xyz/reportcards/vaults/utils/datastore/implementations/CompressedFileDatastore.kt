package xyz.reportcards.vaults.utils.datastore.implementations

import com.google.gson.GsonBuilder
import xyz.reportcards.vaults.utils.CompressionUtils
import xyz.reportcards.vaults.utils.datastore.Datastore
import java.io.File

private val GSON = GsonBuilder().create()
class CompressedFileDatastore<T: Any>(
    val folder: File,
    val type: Class<T>,
    val fileExtension: String = ".json"
): Datastore<T> {

    init {
        if (!folder.exists()) {
            folder.mkdirs()
        }
        if (!folder.isDirectory) {
            throw IllegalArgumentException("Folder is not a directory")
        }
        if (!folder.canWrite()) {
            throw IllegalArgumentException("Folder is not writable")
        }
    }

    private fun getFile(key: String): File {
        return File(folder, key + fileExtension)
    }

    override fun get(key: String): T? {
        val file = getFile(key)
        if (!file.exists()) {
            return null
        }
        val compressedBytes = file.readBytes()
        val decompressedBytes = CompressionUtils.decompress(compressedBytes)
        val json = decompressedBytes.decodeToString()
        return GSON.fromJson(json, type)
    }

    override fun set(key: String, value: T): Boolean {
        val file = getFile(key)
//        if (file.exists()) {
//            return false
//        }
        val json = GSON.toJson(value)
        val jsonBytes = json.toByteArray()
        val compressedBytes = CompressionUtils.compress(jsonBytes)
        file.writeBytes(compressedBytes)

        //println("Wrote ${file.name}: ${file.length()}")
        //println("Compressed size: ${file.length().toDouble() / 1024 / 1024} MB")
        //println("Original Content: $json")
        return true
    }

    override fun delete(key: String): Boolean {
        val file = getFile(key)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }

    override fun getKeys(): List<String> {
        return folder.listFiles()?.map { it.name } ?: emptyList()
    }

    override fun getAll(): Map<String, T> {
        return getKeys().associateWith { get(it) ?: throw IllegalStateException("Key $it does not exist") }
    }
}