package me.ikevoodoo.lssmp.commands

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.reset
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal class ResetCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if(!sender.hasPermission("lssmp.reset")) {
            sender.getTranslation("commands.reset.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        val toReset: MutableList<OfflinePlayer> = ArrayList()
        if (args.isEmpty()) {
            if (sender is Player) toReset.add(sender)
            else sender.sendMessage("You must specify a player to reset.")
        }
        for (arg in args) {
            if (arg.equals("all", ignoreCase = true) && sender.hasPermission("lssmp.reset.all")) {
                toReset.clear()
                toReset.addAll(Bukkit.getOnlinePlayers())
                Bukkit.getOfflinePlayers().forEach {
                    LSSMP.PLAYER_HEALTH_STORAGE[it.uniqueId.toString()] = "RESET"
                }
                LSSMP.PLAYER_HEALTH_STORAGE.save()
                break
            }
            val player = Bukkit.getOfflinePlayer(arg)
            toReset.add(player)
        }
        for (player in toReset) {
            if(player.isOnline) {
                player.player?.reset()
            } else {
                LSSMP.PLAYER_HEALTH_STORAGE[player.uniqueId.toString()] = "RESET"
            }
        }
        LSSMP.PLAYER_HEALTH_STORAGE.save()
        val sb = StringBuilder()
        var i = 0
        while (i < toReset.size && i < 5) {
            sb.append(toReset[i].name).append(", ")
            i++
        }
        sender.sendMessage("Reset " + toReset.size + " players. Players: " + sb.toString().replace(", $".toRegex(), ""))
        return true
    }
}