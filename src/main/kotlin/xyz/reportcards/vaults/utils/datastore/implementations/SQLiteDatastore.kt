package xyz.reportcards.vaults.utils.datastore.implementations

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import xyz.reportcards.vaults.utils.CompressionUtils
import xyz.reportcards.vaults.utils.datastore.Datastore
import java.io.File
import java.sql.DriverManager
import java.util.concurrent.TimeUnit

class SQLiteDatastore<T : Any>(
    private val dbFile: File,
    private val type: Class<T>,
    private val tableName: String,
    cacheDurationMinutes: Long,
    private val plugin: JavaPlugin
) : Datastore<T> {

    private val gson = Gson()
    private val cache: Cache<String, T> = CacheBuilder.newBuilder()
        .expireAfterWrite(cacheDurationMinutes, TimeUnit.MINUTES)
        .build()

    init {
        Class.forName("org.sqlite.JDBC")
        createTableIfNotExists()
    }

    private fun createTableIfNotExists() {
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
            connection.createStatement().use { stmt ->
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS $tableName (key TEXT PRIMARY KEY, data BLOB)")
            }
        }
    }

    override fun get(key: String): T? {
        cache.getIfPresent(key)?.let { return it }
        return getDataFromDatabase(key)?.also { cache.put(key, it) }
    }

    private fun getDataFromDatabase(key: String): T? {
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
            connection.prepareStatement("SELECT data FROM $tableName WHERE key = ?").use { stmt ->
                stmt.setString(1, key)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val compressedData = rs.getBytes("data")
                    val jsonData = CompressionUtils.decompress(compressedData).toString(Charsets.UTF_8)
                    val value = gson.fromJson(jsonData, type)
                    cache.put(key, value) // Cache the decompressed value
                    return value
                }
            }
        }
        return null
    }

    override fun set(key: String, value: T): Boolean {
        cache.put(key, value) // Store decompressed value in cache
        scheduleDatabaseWrite(key, value)
        return true
    }

    private fun scheduleDatabaseWrite(key: String, value: T) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
                val jsonData = gson.toJson(value)
                val compressedBytes = CompressionUtils.compress(jsonData.toByteArray(Charsets.UTF_8))
                connection.prepareStatement("INSERT OR REPLACE INTO $tableName (key, data) VALUES (?, ?)").use { stmt ->
                    stmt.setString(1, key)
                    stmt.setBytes(2, compressedBytes)
                    stmt.executeUpdate()
                }
            }
        })
    }

    override fun delete(key: String): Boolean {
        cache.invalidate(key) // Remove from cache
        scheduleDatabaseDelete(key)
        return true
    }

    private fun scheduleDatabaseDelete(key: String) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
                connection.prepareStatement("DELETE FROM $tableName WHERE key = ?").use { stmt ->
                    stmt.setString(1, key)
                    stmt.executeUpdate()
                }
            }
        })
    }

    override fun getKeys(): List<String> {
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
            connection.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT key FROM $tableName")
                return generateSequence { if (rs.next()) rs.getString("key") else null }.toList()
            }
        }
    }

    override fun getAll(): Map<String, T> {
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
            connection.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT key, data FROM $tableName")
                val result = mutableMapOf<String, T>()
                while (rs.next()) {
                    val key = rs.getString("key")
                    val compressedData = rs.getBytes("data")
                    val jsonData = CompressionUtils.decompress(compressedData).toString(Charsets.UTF_8)
                    val value = gson.fromJson(jsonData, type)
                    result[key] = value
                    cache.put(key, value) // Cache decompressed value
                }
                return result
            }
        }
    }
}