package me.ikevoodoo.lssmp.commands.completion

import me.ikevoodoo.lssmp.LSSMP
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.util.*

class ReviveCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val names = mutableListOf<String>()

        if(args.isEmpty()) {
            names.add("all")
            return names
        }

        LSSMP.ELIMINATION_STORAGE.getAllKeys().forEach {
            Bukkit.getOfflinePlayer(UUID.fromString(it)).name?.let { name -> names.add(name) }
        }

        LSSMP.TO_ELIMINATE.getAllKeys().forEach {
            Bukkit.getOfflinePlayer(UUID.fromString(it)).name?.let { name -> names.add(name) }
        }

        return names
    }
}