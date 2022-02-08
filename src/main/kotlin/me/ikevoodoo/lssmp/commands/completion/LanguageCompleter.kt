package me.ikevoodoo.lssmp.commands.completion

import me.ikevoodoo.lssmp.TranslationManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class LanguageCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        var languages = TranslationManager.getLanguageNames()

        if (args.isEmpty()) {
            return languages
        }

        val arg = args[0]
        languages = languages.filter { it.startsWith(arg, true) } as MutableList<String>
        return languages
    }


}