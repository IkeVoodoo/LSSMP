package me.ikevoodoo.lssmp.commands

import me.ikevoodoo.lssmp.TranslationManager
import me.ikevoodoo.lssmp.TranslationManager.Companion.convertLanguage
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class LanguageCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.language")) {
            sender.getTranslation("commands.language.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        if(args.isEmpty() || args[0] == "clear") {
            TranslationManager.setForcedLanguage(null)
            sender.getTranslation("language.cleared")?.let { sender.sendMessage(it) } ?: sender.sendMessage("Language cleared")
            return true
        }

        val converted = args[0].convertLanguage()

        if(!TranslationManager.getLanguages().contains(converted)) {
            sender.sendMessage("${sender.getTranslation("language.not-found")?.replace("%language%", args[0])}")
            return true
        }

        TranslationManager.setForcedLanguage(converted)
        sender.sendMessage("${sender.getTranslation("language.set")?.replace("%language%", args[0])}")
        return true;
    }
}