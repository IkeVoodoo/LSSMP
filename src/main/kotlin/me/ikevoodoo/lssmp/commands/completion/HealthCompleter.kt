package me.ikevoodoo.lssmp.commands.completion

import me.ikevoodoo.lssmp.utils.CompletionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class HealthCompleter: TabCompleter {
    val commands = mutableListOf("get", "set", "add", "sub")

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if(args.isEmpty()) {
            return commands
        }

        if(args.size == 1) {
            return commands.filter { it.startsWith(args[0]) }.toMutableList()
        }

        when(args[0]) {
            "get", "set" -> {
                if(args.size == 2) {
                    return CompletionUtils.getPlayerNames(includeEliminated = true).filter { it.startsWith(args[1]) }.toMutableList()
                }
            }
            "add", "sub" -> {
                return CompletionUtils.getPlayerNames(includeEliminated = true).let {
                    if(!args.contains("-a")) {
                        it.add("-a")
                        if (!args.contains("--parse")) it.add("--parse")
                        if (!args.contains("-p")) it.add("-p")
                        if (!args.contains("-l")) it.add("-l")
                        if (!args.contains("--limit")) it.add("--limit")
                    }
                    it
                }.filter { it.startsWith(args[args.size - 1].lowercase()) }.toMutableList()
            }
        }

        return mutableListOf()
    }
}