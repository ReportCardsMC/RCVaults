package xyz.reportcards.vaults.utils.datastore

interface Datastore<T> {

    fun get(key: String): T?
    fun set(key: String, value: T): Boolean
    fun delete(key: String): Boolean

    fun getKeys(): List<String>
    fun getAll(): Map<String, T>

}