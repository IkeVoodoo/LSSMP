package me.ikevoodoo.lssmp.commands

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.convertHearts
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.getMaxHealthAttribute
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.modifyHealth
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.setMaxHealthAttribute
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class HealthCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.health")) {
            sender.getTranslation("commands.health.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        if(args.isEmpty()) {
            sender.sendMessage("/lshealth <get|set|add|sub> <player> [<amount>]")
            return true
        }

        if(args[0].lowercase() == "get") {
            if(args.size < 2) {
                sender.sendMessage("/lshealth get <player>")
                return true
            }

            val player = Bukkit.getPlayer(args[1])
            if(player == null) {
                sender.sendMessage("Player not found")
                return true
            }

            sender.sendMessage("${player.name}'s max health is ${player.getMaxHealthAttribute()}")
            return true
        }

        if(args[0].lowercase() == "add" || args[0].lowercase() == "sub") {
            if(args.size < 3) {
                sender.sendMessage("/lshealth ${args[0]} <player> [<amount>]")
                return true
            }

            val flags = mutableListOf<String>()
            val args2 = args.drop(1).filter {
                if(it.startsWith("-")) {
                    !flags.add(it.substring(1).lowercase())
                } else true
            }

            val player = Bukkit.getPlayer(args2[0])
            if(player == null) {
                sender.sendMessage(sender.getTranslation("commands.errors.player-not-found")?.replace("%player%", args2[0])?.color() ?: "Player not found")
                return true
            }

            val amount = args2[1].toDouble()
            player.setMaxHealthAttribute(
                player.getMaxHealthAttribute()?.plus(
                    (if(args[0].equals("add", true)) amount else -amount).let {
                        if(flags.contains("p") || flags.contains("parse") || flags.contains("a")) it.convertHearts() else it
                    }
                ).let { num ->
                    val max = (
                            if(LSSMP.INSTANCE.config.contains("elimination.maxHearts"))
                                LSSMP.INSTANCE.config.getDouble("elimination.maxHearts")
                            else 10.0
                            ).convertHearts()

                    num.takeUnless {
                        LSSMP.INSTANCE.config.getBoolean("elimination.useMaxHealth")
                                && it!! >= max
                                && (flags.contains("l") || flags.contains("limit") || flags.contains("a"))
                    }.let { it ?: max }
                }
            )
            /*
            player.setMaxHealthAttribute((player.getMaxHealthAttribute()
                ?.plus(args[2].toDouble().let {
                    return@let (if(args[0].lowercase() == "add") it else -it).convertHearts()
                }.let {
                val max = (if(Main.INSTANCE.config.contains("elimination.maxHearts"))Main.INSTANCE.config.getDouble("elimination.maxHearts")
                else 10.0).convertHearts()
                if(Main.INSTANCE.config.getBoolean("elimination.useMaxHealth")
                    && it >= max
                    && (args.contains("-l") || args.contains("--limit")))
                    return@let max
                it
            })*/

            player.modifyHealth(amount, args[0].equals("sub", true))

            sender.sendMessage(sender.getTranslation("commands.health.set")
                ?.replace("%player%", player.name)
                ?.replace("%amount%", player.getMaxHealthAttribute().toString())?.color()
                    ?: "${player.name}'s max health is now ${player.getMaxHealthAttribute()}hp")
            return true
        }

        if(args[0].lowercase() == "set") {
            if(args.size < 3) {
                sender.sendMessage("/lshealth set <player> [<amount>]")
                return true
            }

            val player = Bukkit.getPlayer(args[1])
            if(player == null) {
                sender.sendMessage(sender.getTranslation("commands.errors.player-not-found")?.replace("%player%", args[1])?.color() ?: "Player not found")
                return true
            }

            val amount = args[2].toDouble().convertHearts()

            player.setMaxHealthAttribute(amount)
            player.health = amount

            sender.sendMessage(sender.getTranslation("commands.health.set")
                ?.replace("%player%", player.name)
                ?.replace("%amount%", player.getMaxHealthAttribute().toString())?.color()
                ?: "${player.name}'s max health is now ${player.getMaxHealthAttribute()}hp")
            return true
        }
        return true;
    }
}