package me.ikevoodoo.lssmp.utils

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.entity.Player
import java.lang.reflect.Field

class ReflectionUtils {

    companion object {
        private var commandMapField: Field
        private var commandMap: CommandMap

        init {
            try {
                commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")

                commandMapField.isAccessible = true

                commandMap = commandMapField.get(Bukkit.getServer()) as CommandMap
            } catch (e: Exception) {
                throw RuntimeException("Unable to get command map", e)
            }
        }

        fun Player.getLanguage(): String {
            val handle = getHandle()
            val f: Field = handle.javaClass.getDeclaredField("locale")
            f.isAccessible = true
            return f.get(handle) as String
        }

        fun Player.getHandle(): Any {
            return javaClass.getMethod("getHandle").invoke(this)
        }

        fun String.isCommand(): Boolean {
            return commandMap.getCommand(this) != null
        }
    }

}