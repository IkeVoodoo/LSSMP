package me.ikevoodoo.lssmp.storage

import me.ikevoodoo.lssmp.utils.StorageUtils

class SimpleStorage(path: String) : AbstractStorage(path) {
    private val storage = HashMap<String, Any>()

    init {
        load()
    }

    override fun get(key: String): Any? {
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

    override fun getAllValues(): List<Any?> {
        return storage.values.toList()
    }

    override operator fun set(key: String, value: Any) {
        storage[key] = value
    }

    override fun set(key: String) {
        storage[key] = "NoValue"
    }

    override fun iterator(): Iterator<String> {
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
        var readKey = false
        var key = ""
        var value: String
        for(line in reader.lineSequence().iterator()) {
            if(line.isBlank() || line.isEmpty() || line.startsWith(";\t")) {
                continue
            }
            if (!readKey) {
                readKey = true
                key = line
            } else {
                readKey = false
                value = line

                val v: Any = try {
                    value.parseList()
                } catch (e: Exception) {
                    value.parse()
                }
                storage[key] = v
            }
        }
    }

    override fun save() {
        if(!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        val writer = file.outputStream().bufferedWriter()
        StorageUtils.writeSimpleFormat(writer, storage)
        writer.close()
    }

}

private fun String.parseList(): List<Any> {
    if(!this.startsWith("[") && !this.endsWith("]")) throw IllegalArgumentException("String is not a list")
    val list = mutableListOf<Any>()
    val buf = StringBuffer()
    var lastChar = ' '
    var isInString = false
    for(char in this.replaceFirst("[", "")) {
        if(char == '"' && lastChar != '\\') {
            val oldIsInString = isInString
            isInString = !isInString
            if(oldIsInString != oldIsInString && !isInString) {
                list.add(buf.toString())
                buf.setLength(0)
            }
            lastChar = char
            continue
        }

        if(isInString) {
            buf.append(char)
            lastChar = char
            continue
        }

        if(char == ' ' && lastChar == ',') {
            lastChar = char
            continue
        }

        if(char == ',' || char == ']') {
            list.add(buf.toString().parse())
            buf.setLength(0)
            lastChar = char
            continue
        }
        buf.append(char)
        lastChar = char
    }
    return list
}

private fun String.parse(): Any {
    return try {
        toInt()
    } catch (e: Exception) {
        try {
            toDouble()
        } catch (e: Exception) {
            try {
                toBooleanStrict()
            } catch (e: Exception) {
                try {
                    toLong()
                } catch (e: Exception) {
                    this
                }
            }
        }
    }
}
