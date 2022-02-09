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

                        try {
                            closestMaterial(getString("recipe.slots.$key.item")!!
                                .uppercase()
                                .replace("([a-zA-Z0-9])\\s+".toRegex(), "$1_")
                                .replace("_+".toRegex(), "_")
                                .replace("-+".toRegex(), "_")
                                .replace("MINECRAFT:", "")
                                .replace(":", "")
                                .replace("^\\s+".toRegex(), "")
                                .replace("\\s+$".toRegex(), "")
                            )
                        } catch (e: Exception) {
                            LSSMP.INSTANCE.logger.severe(e.message)
                            Material.AIR
                        }
                }
            }

            for (i in 1..9) {
                set("recipe.slots.$i.item", slots[if(i - 1 <= 0) 0 else if(i <= 9) i - 1 else 8].toString()
                    .lowercase()
                    .replace("([a-zA-Z0-9])_".toRegex(), "$1 ")
                    .replace("-+".toRegex(), "_")
                    .replace("minecraft:", "")
                    .replace(":", "")
                )
            }

            return HeartRecipe(enabled, outputAmount, shaped, slots)
        }

        fun closestMaterial(name: String): Material {
            return try {
                Material.valueOf(name)
            } catch (e: Exception) {
                val distances = ArrayList<Pair<Int, String>>()
                for(material in Material.values()) {
                    distances.add(distance(material.name, name) to material.name)
                }

                val min = distances.minByOrNull { it.first }
                    ?: throw IllegalStateException("No material found for $name, defaulting to AIR")

                distances.sortBy { it.first }

                val second = distances[1]

                if(min.first > 5 && second.first - min.first < 6) throw IllegalStateException("No material found for $name, defaulting to AIR")

                Material.valueOf(min.second)
            }
        }
        
        fun distance(str1: String, str2: String): Int {
            val len1 = str1.length
            val len2 = str2.length
            val dp = Array(len1 + 1) { IntArray(len2 + 1) }

            for (i in 0..len1) {
                dp[i][0] = i
            }

            for (j in 0..len2) {
                dp[0][j] = j
            }

            for (i in 1..len1) {
                for (j in 1..len2) {
                    val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                    dp[i][j] = min(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1,
                        dp[i - 1][j - 1] + cost
                    )
                }
            }

            return dp[len1][len2]
        }

        fun min(vararg numbers: Int): Int {
            var min = numbers[0]
            for (number in numbers) {
                if (number < min) {
                    min = number
                }
            }
            return min
        }
    }


}