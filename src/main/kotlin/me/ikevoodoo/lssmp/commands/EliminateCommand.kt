package me.ikevoodoo.lssmp.commands

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.tryEliminate
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class EliminateCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.eliminate")) {
            sender.getTranslation("commands.eliminate.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        if(args.isEmpty()) {
            sender.sendMessage((sender.getTranslation("commands.no-args") ?: "&cYou must specify at least player!").toString())
            return true
        }

        if(args.contains("all")) {
            for(player in Bukkit.getOnlinePlayers())
                player.tryEliminate(false, null, true)

            for(player in Bukkit.getOfflinePlayers())
                LSSMP.TO_ELIMINATE[player.uniqueId.toString()] = ""

            sender.sendMessage("&aAll players have been eliminated.".color())
        }

        args.forEach {
            val player = Bukkit.getOfflinePlayer(it)
            if(player.isOnline) player.player?.tryEliminate(false, null, true)
            else LSSMP.TO_ELIMINATE[player.uniqueId.toString()] = ""
        }

        LSSMP.TO_ELIMINATE.save()

        sender.sendMessage("Eliminated ${args.size} players!")
        return true;
    }
}