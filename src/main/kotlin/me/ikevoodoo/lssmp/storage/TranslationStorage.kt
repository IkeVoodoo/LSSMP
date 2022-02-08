package me.ikevoodoo.lssmp.storage

import me.ikevoodoo.lssmp.utils.StorageUtils

class TranslationStorage(path: String, var language: String, var name: String) : AbstractStorage(path) {

    private val storage = HashMap<String, String>()

    init {
        load()
    }

    override fun get(key: String): String? {
        return storage[key]
    }

    override fun getAll(): List<String> {
        val list = mutableListOf<String>()
        storage.forEach {
            list.add(it.key + "=" + it.value)
        }
        return list.toList()
    }

    override fun getAllKeys(): List<String> {
        return storage.keys.toList()
    }

    override fun getAllValues(): List<String?> {
        return storage.values.toList()
    }

    override operator fun set(key: String, value: Any) {
        storage[key] = value.toString()
    }

    override fun set(key: String) {
        storage[key] = "NoValue"
    }

    override fun iterator(): Iterator<Any> {
        return getAllKeys().iterator()
    }

    override fun delete(key: String) {
        storage.remove(key)
    }

    override fun clear() {
        storage.clear()
    }

    override fun contains(key: String): Boolean {
        return storage.containsKey(key)
    }

    override fun load() {
        if(!file.exists()) return
        clear()
        val reader = file.inputStream().bufferedReader(Charsets.UTF_8)
        var readLang = false
        var readName = false
        var readKey = false
        var key = ""
        for(line in reader.lineSequence().iterator()) {
            if(line.isBlank() || line.isEmpty())
                continue
            if(!readLang && line.startsWith("!")) {
                language = line.substring(1)
                readLang = true
                continue
            }
            if(!readName && line.startsWith("@")) {
                name = line.substring(1)
                readName = true
                continue
            }
            if (!readKey) {
                readKey = true
                key = line
            } else {
                readKey = false
                storage[key] = line
            }
        }
    }

    override fun save() {
        if(!file.exists()) {
            file.mkdirs()
            file.delete()
            file.createNewFile()
        }
        val writer = file.outputStream().bufferedWriter()
        writer.write("!$language\n@$name\n")
        StorageUtils.writeSimpleFormat(writer, storage)
        writer.close()
    }


}