package me.ikevoodoo.lssmp.utils

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.convertHearts
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.setMaxHealthAttribute
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.BufferedWriter
import java.util.*

class ExtensionUtils {

    companion object {
        fun String.color(): String {
            return ChatColor.translateAlternateColorCodes('&', this)
        }

        fun BufferedWriter.write(toByteArray: ByteArray) {
            toByteArray.iterator().forEach { byte -> this.write(byte.toInt()) }
        }

        operator fun ChatColor.plus(other: ChatColor): String {
            return this.toString() + other.toString()
        }

        operator fun ChatColor.plus(other: String): String {
            return this.toString() + other
        }

        fun World.isAllowed(): Boolean {
            return (
                    if(!LSSMP.INSTANCE.config.contains("elimination.allowedWorlds")) return true else {
                        LSSMP.INSTANCE.config.getStringList("elimination.allowedWorlds").containsLowercase("all") ||
                                LSSMP.INSTANCE.config.getStringList("elimination.allowedWorlds").contains(this.name)
                    }
                    )
        }

        fun List<String>.containsLowercase(string: String): Boolean {
            return this.any { it.lowercase(Locale.getDefault()) == string.lowercase(Locale.getDefault()) }
        }

        fun Player.reset() {
            val reviveMaxHealth = LSSMP.INSTANCE.config.get("events.revived.maxHealth")
            val amount: Double = if ("RESET" == reviveMaxHealth) {
                20.0
            } else {
                try {
                    reviveMaxHealth.toString().toDouble().convertHearts()
                } catch (e: Exception) {
                    20.0
                }
            }
            reset(amount)
        }

        fun Player.reset(amount: Double) {
            setMaxHealthAttribute(amount)
            health = amount
        }

        fun Double.random(max: Double): Double {
            return (Math.random() * (max - this + 1) + this)
        }

        fun FileConfiguration.parseRecipe(): HeartRecipe {
            val enabled = getBoolean("options.enabled")
            if(!enabled) return HeartRecipe(false, 0, false, Array(9) { Material.AIR })

            val outputAmount = getInt("options.outputAmount")
            val shaped = getBoolean("options.shaped")

            val slots = Array(9) { Material.AIR }

            if(isConfigurationSection("recipe.slots")) {
                for(key in getConfigurationSection("recipe.slots")?.getKeys(false)!!) {
                    slots[key.toInt().let { if(it - 1 <= 0) 0 else if(it <= 9) it - 1 else 8}] =
                        Material.valueOf(
                            getString("recipe.slots.$key.item")!!
                                .uppercase()
                                .replace("([a-zA-Z0-9])\\s+".toRegex(), "$1_")
                                .replace("-+".toRegex(), "_")
                                .replace("MINECRAFT:", "")
                                .replace(":", "")
                                .replace("^\\s+".toRegex(), "")
                                .replace("\\s+$".toRegex(), "")
                        )
                }
            }


            return HeartRecipe(enabled, outputAmount, shaped, slots)
        }
    }


}