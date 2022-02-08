package me.ikevoodoo.lssmp.utils

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.isAllowed
import me.ikevoodoo.lssmp.utils.PlaceholderUtils.Companion.parsePlaceholders
import me.ikevoodoo.lssmp.utils.ReflectionUtils.Companion.isCommand
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class EliminationUtils {

    companion object {
        private val eliminationPlaceholders = mutableMapOf<String, Any>()
        private val revivePlaceholders = mutableMapOf<String, Any>()

        fun Double.convertHearts(): Double {
            return this + this.toString().replaceFirst("\\.0+$".toRegex(), "").toDouble()
        }

        fun String.convertHearts(): Double {
            return this.toDouble().convertHearts()
        }

        fun parseBanTime(t: String): Long {
            val time = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS").parse(t).query(TemporalQueries.localTime())
            return ((time.hour.hours.toLong(DurationUnit.MILLISECONDS) + time.minute.minutes.toLong(DurationUnit.MILLISECONDS) + time.second.seconds.toLong(
                DurationUnit.MILLISECONDS) + (time.nano / 100000).toLong()).toFloat() / 1000 * 20).toLong()
        }

        fun Player.getEliminationHealthScale(): Double {
            return (
                    if(killer == null) {
                        if(!LSSMP.INSTANCE.config.contains("elimination.environmentHealthScale")) 1.0
                        else LSSMP.INSTANCE.config.getDouble("elimination.environmentHealthScale")
                    }
                    else {
                        if(!LSSMP.INSTANCE.config.contains("elimination.healthScale")) 1.0
                        else LSSMP.INSTANCE.config.getDouble("elimination.healthScale")
                    })
                .convertHearts()
        }

        fun Player.decreaseMaxHealth(isWithdraw: Boolean = false, attacker: Player? = killer) {
            if(attacker == null && !LSSMP.INSTANCE.config.getBoolean("elimination.environmentStealsHearts")
                && !isWithdraw)
                return

            val maxHealth = getMaxHealthAttribute()
            if(maxHealth == null) {
                kickPlayer("Max health is null")
                return
            }

            val scale = getEliminationHealthScale()
            setMaxHealthAttribute(maxHealth - scale)
            modifyHealth(scale, true)
        }

        fun Player.increaseMaxHealth(scale: Double = 2.0) {
            val maxHealth = getMaxHealthAttribute()
            if(maxHealth == null) {
                kickPlayer("Max health is null")
                return
            }

            val max = (
                    if(LSSMP.INSTANCE.config.contains("elimination.maxHearts"))
                        LSSMP.INSTANCE.config.getDouble("elimination.maxHearts")
                    else 10.0
                    ).convertHearts()
            if(LSSMP.INSTANCE.config.getBoolean("elimination.useMaxHealth") && maxHealth + scale >= max) {
                setMaxHealthAttribute(max)
                health = max
                return
            }

            setMaxHealthAttribute(maxHealth + scale)
            modifyHealth(scale)
        }

        fun Player.increaseKillerHealth(attacker: Player? = killer) {
            if(attacker == null) return

            val maxHealth = attacker.getMaxHealthAttribute()
            if(maxHealth == null) {
                attacker.kickPlayer("Max health is null")
                return
            }

            val scale = getEliminationHealthScale()
            attacker.increaseMaxHealth(scale)
        }

        fun Player.modifyHealth(amount: Double, sub: Boolean = false) {
            if(!LSSMP.INSTANCE.config.getBoolean("elimination.scaleHealth"))
                return
            val newHealth = if(sub) health - amount else health + amount
            if(newHealth < 0) return
            if(newHealth > getMaxHealthAttribute()!!) {
                health = getMaxHealthAttribute()!!
                return
            }
            health = newHealth
        }

        fun Player.tryEliminate(isWithdraw: Boolean = false, attacker: Player? = killer, forced: Boolean = false): Boolean {
            if(!world.isAllowed() && !isWithdraw && !forced)
                return false

            if(attacker == null && !LSSMP.INSTANCE.config.getBoolean("elimination.environmentStealsHearts")
                && !isWithdraw && !forced)
                return false

            val maxHealth = getMaxHealthAttribute()
            if(maxHealth == null) {
                kickPlayer("Max health is null")
                return false
            }

            if(maxHealth <= 0 || forced)
                return eliminate(attacker)

            return false
        }

        fun Player.getMaxHealthAttribute(): Double? {
            return getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue
        }

        fun Player.setMaxHealthAttribute(value: Double) {
            getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = value
        }

        fun OfflinePlayer.tryRevive() {
            LSSMP.ELIMINATION_STORAGE.delete(uniqueId.toString())
            LSSMP.BAN_TIMES.delete(uniqueId.toString())
            LSSMP.PLAYER_HEALTH_STORAGE.delete(uniqueId.toString())
            LSSMP.TO_ELIMINATE.delete(uniqueId.toString())
            LSSMP.TARGET_STORAGE.delete(uniqueId.toString());

            LSSMP.INSTANCE.saveStorage()

            revivePlaceholders["%player%"] = name ?: "Unknown"
            revivePlaceholders["%player uuid%"] = uniqueId.toString()
            revivePlaceholders["%last played%"] = lastPlayed
            revivePlaceholders["%first played%"] = firstPlayed

            for(cmd in LSSMP.INSTANCE.config.getStringList("events.revived.commands"))
                cmd.parsePlaceholders(revivePlaceholders).let {
                    if(it.isCommand())
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it)
                }
        }

        fun Player.eliminate(attacker: Player? = killer): Boolean {
            if(!LSSMP.INSTANCE.config.getBoolean("elimination.shouldEliminate"))
                return false

            eliminationPlaceholders["%player%"] = name
            eliminationPlaceholders["%hasKiller%"] = attacker != null
            eliminationPlaceholders["%killer%"] = attacker?.name ?: "Environment"
            eliminationPlaceholders["%killer uuid%"] = attacker?.uniqueId?.toString() ?: ""
            eliminationPlaceholders["%killer health%"] = attacker?.health?.toInt() ?: 0
            eliminationPlaceholders["%killer max health%"] = attacker?.getMaxHealthAttribute()?.toInt() ?: 0
            eliminationPlaceholders["%player uuid%"] = uniqueId.toString()
            eliminationPlaceholders["%player health%"] = health.toInt()
            eliminationPlaceholders["%player max health%"] = getMaxHealthAttribute()?.toInt() ?: 0
            eliminationPlaceholders["%world%"] = world.name
            eliminationPlaceholders["%world uuid%"] = world.uid.toString()
            eliminationPlaceholders["%world difficulty%"] = world.difficulty.toString()
            eliminationPlaceholders["%world time%"] = world.time.toInt()
            eliminationPlaceholders["%world weather duration%"] = world.weatherDuration

            // Ignore the exception, since somehow the engine is still null after I check 1856564 times
            for (cmd in LSSMP.INSTANCE.config.getStringList("events.eliminated.commands"))
                cmd.parsePlaceholders(eliminationPlaceholders).let {
                    if(it.isCommand())
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it)
                }

            val attackerDisplayName = attacker?.displayName ?: getTranslation("environment.displayName") ?: "Environment"

            LSSMP.ELIMINATION_STORAGE[uniqueId.toString()] = attackerDisplayName
            attacker?.uniqueId.takeUnless { it == null }.let {
                LSSMP.TARGET_STORAGE[uniqueId.toString()] = it.toString()
            }

            LSSMP.ELIMINATION_STORAGE.save()
            LSSMP.TARGET_STORAGE.save()

            if(LSSMP.INSTANCE.config.getBoolean("elimination.bans.shouldBan")) {
                kickPlayer(LSSMP.INSTANCE.config.getString("elimination.bans.banMessage")?.replace("%player%", attackerDisplayName)?.color()
                    ?: "You have been eliminated!")

                if(LSSMP.INSTANCE.config.getBoolean("elimination.bans.usingBanTime")) {
                    LSSMP.INSTANCE.config.getString("elimination.bans.banTime")?.let { parseBanTime(it) }?.let {
                        LSSMP.BAN_TIMES[uniqueId.toString()] = /*System.currentTimeMillis() +*/ it
                    }
                }

                if(LSSMP.INSTANCE.config.getBoolean("elimination.bans.broadcastBan")) {
                    LSSMP.INSTANCE.config.getString("elimination.bans.broadcastMessage")?.replace("%player%", displayName)?.color()
                        ?.let { Bukkit.broadcastMessage(it) }
                }
                return false
            }
            else if(LSSMP.INSTANCE.config.getBoolean("elimination.spectate.shouldSpectate")) {
                gameMode = GameMode.SPECTATOR
                resetHealth()
                return true
            }

            return false
        }

        fun Player.resetHealth() {
            val maxHealth = getMaxHealthAttribute()
            if(maxHealth == null)
                kickPlayer("Max health is null")
            setMaxHealthAttribute(20.0)
        }

        fun Player.isEliminated(): Boolean {
            return LSSMP.ELIMINATION_STORAGE.contains(uniqueId.toString())
        }

        fun UUID.isEliminated(): Boolean {
            return LSSMP.ELIMINATION_STORAGE.contains(toString())
        }
    }
}
