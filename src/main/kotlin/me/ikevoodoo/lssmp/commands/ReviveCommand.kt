package me.ikevoodoo.lssmp.commands

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.tryRevive
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.reset
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ReviveCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.revive")) {
            sender.getTranslation("commands.revive.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        if(args.isEmpty()) {
            sender.sendMessage((sender.getTranslation("commands.no-args") ?: "&cYou must specify at least player!").toString())
            return true
        }

        if(args.contains("all")) {
            LSSMP.ELIMINATION_STORAGE.clear()
            LSSMP.TARGET_STORAGE.clear()
            LSSMP.BAN_TIMES.clear()
            LSSMP.PLAYER_HEALTH_STORAGE.clear()
            LSSMP.TO_ELIMINATE.clear()

            LSSMP.INSTANCE.saveStorage()

            sender.sendMessage("&aAll players have been revived.".color())
            return true
        }

        args.forEach {
            val player = Bukkit.getOfflinePlayer(it)
            player.tryRevive()
            if(player.isOnline) player.player?.reset()
            else LSSMP.PLAYER_HEALTH_STORAGE[player.uniqueId.toString()] = "RESET"
        }

        sender.sendMessage("Revived ${args.size} players!")

        return true
    }
}