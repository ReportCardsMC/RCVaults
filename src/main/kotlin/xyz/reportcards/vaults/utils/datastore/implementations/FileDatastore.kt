package xyz.reportcards.vaults.utils.datastore.implementations

import com.google.gson.GsonBuilder
import xyz.reportcards.vaults.utils.datastore.Datastore
import java.io.File

private val GSON = GsonBuilder().create()

class FileDatastore<T: Any>(
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
        return GSON.fromJson(file.readText(), type)
    }

    override fun set(key: String, value: T): Boolean {
        val file = getFile(key)
//        if (file.exists()) {
//            return false
//        }
        file.writeText(GSON.toJson(value))
        return true
    }

    override fun delete(key: String): Boolean{
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