package me.ikevoodoo.lssmp.commands

import UpdateData
import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class VersionCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.version")) {
            sender.getTranslation("commands.version.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        if(UpdateData.UPDATE_AVAILABLE) {
            sender.sendMessage(
                LSSMP.PREFIX + sender.getTranslation("commands.version.message")
                    ?.replace("%version-color%", if (UpdateData.UPDATE_AVAILABLE) "&a" else "&c")
                    ?.replace("%version%",
                        (if (UpdateData.UPDATE_AVAILABLE) UpdateData.VERSION else LSSMP.INSTANCE.description.version)
                            ?: "Unknown"
                    )
                    ?.replace(
                        "%status%",
                        if (UpdateData.UPDATE_AVAILABLE) "(${sender.getTranslation("commands.version.status.outdated")})"
                        else "(${sender.getTranslation("commands.version.status.latest")})"
                    )?.color()
            )

        }
        return true;
    }
}