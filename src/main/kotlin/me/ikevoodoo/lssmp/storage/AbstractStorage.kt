package me.ikevoodoo.lssmp.storage

import java.io.File

abstract class AbstractStorage(private val path: String) {

    val file: File = File(path);

    abstract fun getAll(): List<String>

    abstract fun getAllKeys(): List<String>
    abstract fun getAllValues(): List<Any?>

    abstract operator fun set(key: String, value: Any)

    abstract operator fun iterator(): Iterator<Any>

    abstract fun set(key: String)

    abstract operator fun get(key: String): Any?

    abstract fun delete(key: String)

    abstract fun clear()

    abstract fun contains(key: String): Boolean

    abstract fun load()

    abstract fun save()

}