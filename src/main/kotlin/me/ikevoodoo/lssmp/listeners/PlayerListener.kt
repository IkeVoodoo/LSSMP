package me.ikevoodoo.lssmp.listeners

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.convertHearts
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.decreaseMaxHealth
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.getMaxHealthAttribute
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.increaseKillerHealth
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.increaseMaxHealth
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.isEliminated
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.setMaxHealthAttribute
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.tryEliminate
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.isAllowed
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.reset
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.getCustomModelData
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.getName
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setCustomModelData
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setName
import me.ikevoodoo.lssmp.utils.PlaceholderUtils.Companion.parsePlaceholders
import me.ikevoodoo.lssmp.utils.ReflectionUtils.Companion.isCommand
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*

class PlayerListener: Listener {

    private val placeholders = mutableMapOf<String, Any>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerDied(event: EntityDamageEvent) {
        if(event.isCancelled) return

        if(!event.entity.world.isAllowed())
            return

        if(event.entity !is Player)
            return

        val player = event.entity as Player
        val attacker = if(event is EntityDamageByEntityEvent) event.damager else null

        if(attacker !is Player)
            return

        if(attacker.uniqueId == player.uniqueId)
            return

        if(player.health - event.finalDamage > 0)
            return

        player.decreaseMaxHealth(false, attacker)
        player.increaseKillerHealth(attacker)

        if (player.tryEliminate(false, attacker)) {
            event.isCancelled = true
            return
        }

        placeholders["%player%"] = player.name
        placeholders["%killer%"] = attacker.name
        placeholders["%player health%"] = player.health
        placeholders["%player max health%"] = player.getMaxHealthAttribute() ?: 0
        placeholders["%killer health%"] = attacker.health
        placeholders["%killer max health%"] = attacker.getMaxHealthAttribute() ?: 0
        placeholders["%player uuid%"] = player.uniqueId.toString()
        placeholders["%killer uuid%"] = attacker.uniqueId.toString()
        placeholders["%world%"] = player.world.name
        placeholders["%world uuid%"] = player.world.uid.toString()
        placeholders["%world difficulty%"] = player.world.difficulty.toString()
        placeholders["%world time%"] = player.world.time.toInt()
        placeholders["%world weather duration%"] = player.world.weatherDuration
        placeholders["%damage%"] = event.damage
        placeholders["%final damage%"] = event.finalDamage

        for(cmd in LSSMP.INSTANCE.config.getStringList("events.killed.commands"))
            cmd.parsePlaceholders(placeholders).let {
                if(it.isCommand())
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it)
            }
    }

    @EventHandler
    fun playerColorSheep(event: PlayerInteractEntityEvent) {
        val item = event.player.inventory.getItem(event.hand)
        if(item.getCustomModelData() == LSSMP.HEART_ITEM.getCustomModelData()) {
            event.isCancelled = true

            item.amount = item.amount.minus(1) ?: 0
            event.hand.let { event.player.inventory.setItem(it, item) }

            event.player.increaseMaxHealth()
            event.player.sendMessage(ChatColor.GOLD.toString() + "Healed " + ChatColor.GREEN + "❤ 1")
        }
    }

    @EventHandler
    fun playerUse(event: PlayerInteractEvent) {
        if(event.item?.getCustomModelData() == LSSMP.HEART_ITEM.getCustomModelData()
            && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {
            event.isCancelled = true

            event.item?.amount = event.item?.amount?.minus(1) ?: 0
            event.hand?.let { event.player.inventory.setItem(it, event.item) }

            event.player.increaseMaxHealth()
            event.player.sendMessage(ChatColor.GOLD.toString() + "Healed " + ChatColor.GREEN + "❤ 1")
        }
    }

    @EventHandler
    fun playerQuit(event: PlayerQuitEvent) {
        var connected = LSSMP.CONNECTED_IPS[event.player.address?.address?.hostAddress!!]
        if(connected != null) {
            connected--
            LSSMP.CONNECTED_IPS[event.player.address?.address?.hostAddress!!] = connected
        }
    }

    @EventHandler
    fun playerPreJoined(event: AsyncPlayerPreLoginEvent) {/*
        if((Main.banTimes[event.uniqueId.toString()] ?: Long.MAX_VALUE).toString().toLong() <= System.currentTimeMillis()) {
            Main.banTimes.delete(event.uniqueId.toString())
            Main.eliminationStorage.delete(event.uniqueId.toString())
            Main.playerHealthStorage[event.uniqueId.toString()] = "RESET"
        }*/

        if(event.uniqueId.isEliminated() && LSSMP.INSTANCE.config.getBoolean("elimination.bans.shouldBan")) {
            TranslationManager.getTranslation("eliminated.ban.message")
                ?.let { event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, it) }
                ?: event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are eliminated.")
            return
        }

        var connected = LSSMP.CONNECTED_IPS[event.address.hostAddress]

        if (connected != null) {
            if(!LSSMP.INSTANCE.config.getBoolean("antiAlts.altsAllowed") && connected > LSSMP.INSTANCE.config.getInt("antiAlts.maxAlts")) {
                TranslationManager.getTranslation("antiAlts.kickMessage")
                    ?.let { event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, it) }
                    ?: event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You have too many alts connected!")
                connected--
            }
        }

        LSSMP.CONNECTED_IPS[event.address.hostAddress] = connected?.plus(1) ?: 1
    }



    @EventHandler
    fun playerJoined(event: PlayerJoinEvent) {
        val player = event.player

        if(player.isEliminated() && LSSMP.INSTANCE.config.getBoolean("elimination.bans.shouldBan")) {
            // Ignore join message if the player is eliminated
            event.joinMessage = ""
            TranslationManager.getTranslation("eliminated.ban.message")
                ?.let { player.kickPlayer(it) }
                ?: player.kickPlayer("You are eliminated.")
            return
        }


        if(!player.hasPlayedBefore()) {
            val defaultHearts = LSSMP.INSTANCE.config.get("elimination.defaultHearts")
            var amount = 20.0
            if(defaultHearts is Double)
                amount = defaultHearts.convertHearts()
            /*else if(defaultHearts is String) {
                // If it contains a -, it's a range, if it contains multiple -, evaluate each range
                // and calculate the final range from first value to the last value
                val split = defaultHearts.split("-")
                if(split.size == 1) {
                    amount = split[0].convertHearts()
                } else if(split.size == 2) {
                    val min = split[0].convertHearts()
                    val max = split[1].convertHearts()
                    amount = min.random(max)
                } else {
                    val first = split[0].convertHearts()

                    val stack = Stack<Double>()
                    for(i in 1 until split.size) {
                        val value = split[i].convertHearts()
                        stack.push(value)
                    }

                    while(!stack.isEmpty()) {
                        val v1 = stack.pop()
                        val v2 = stack.pop()
                        stack.add(0, v1.random(v2))
                    }

                    val second = stack.pop()

                    amount = first.coerceAtMost(second).random(first.coerceAtLeast(second))
                }
            }*/
            player.setMaxHealthAttribute(amount)
        }

        if(LSSMP.PLAYER_HEALTH_STORAGE.contains(player.uniqueId.toString())) {
            val health = LSSMP.PLAYER_HEALTH_STORAGE[player.uniqueId.toString()]
            LSSMP.PLAYER_HEALTH_STORAGE.delete(player.uniqueId.toString())
            health.let {
                if(it == "RESET") player.reset()
                else player.setMaxHealthAttribute(it.toString().toDouble())
            }
        }

        if(LSSMP.TO_ELIMINATE.contains(player.uniqueId.toString())) {
            LSSMP.TO_ELIMINATE.delete(player.uniqueId.toString())
            player.tryEliminate(false, null, true)
            return
        }

        // Make sure the name and lore are correct
        event.player.inventory.forEach {
            if(it != null && it.getCustomModelData() == LSSMP.HEART_ITEM.getCustomModelData())
                it.setName(LSSMP.HEART_ITEM.getName()).setCustomModelData(LSSMP.HEART_ITEM.getCustomModelData())
        }
    }

}
