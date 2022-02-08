package me.ikevoodoo.lssmp.commands.completion

import me.ikevoodoo.lssmp.utils.CompletionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EliminateCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return CompletionUtils.getPlayerNames().filter { it.startsWith(args[args.size - 1]) }.toMutableList()
    }
}