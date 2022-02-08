package me.ikevoodoo.lssmp.commands

import UpdateData
import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ChangelogCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.changelog")) {
            sender.getTranslation("commands.changelog.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        if(UpdateData.CHANGELOG_AVAILABLE) {
            sender.sendMessage(LSSMP.PREFIX + sender.getTranslation("commands.changelog.message")?.color())
            sender.sendMessage(UpdateData.CHANGELOG ?: "No changelog could be read from the update server.")
        }
        return true;
    }
}