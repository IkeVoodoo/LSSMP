package me.ikevoodoo.lssmp.utils

class PlaceholderUtils {

    companion object {
        fun String.parsePlaceholders(placeholders: Map<String, Any>): String {
            var result = this
            placeholders.forEach {
                result = result.replace(it.key, it.value.toString())
            }
            return result
        }
    }

}