package me.ikevoodoo.lssmp.utils

import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.write
import java.io.BufferedWriter

class StorageUtils {

    companion object {
        fun writeSimpleFormat(writer: BufferedWriter, storage: HashMap<String, *>) {
            for(pair in storage) {
                val k = pair.key
                val v = pair.value
                writer.write(k.toByteArray(Charsets.UTF_8))
                writer.write("\n")
                writer.write(v.toListString().toByteArray(Charsets.UTF_8))
                writer.write("\n")
            }
        }
    }

}

private fun Any.toListString(): String {
    if(this !is List<*>) return toString()
    val sb = StringBuilder()
    sb.append("[")
    for(i in 0 until this.size) {
        val v = this[i]
        if(v is String) {
            sb.append("\"")
            sb.append(v)
            sb.append("\"")
        } else {
            sb.append(v)
        }
        if(i < this.size - 1) sb.append(", ")
    }
    sb.append("]")
    return sb.toString()
}
