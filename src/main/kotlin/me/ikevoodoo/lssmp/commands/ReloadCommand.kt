package me.ikevoodoo.lssmp.commands

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.plus
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.getCustomModelData
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.getName
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setCustomModelData
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setLore
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setName
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class ReloadCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.reload")) {
            sender.getTranslation("commands.reload.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        LSSMP.INSTANCE.reloadConfig()
        LSSMP.HEART_CONFIG.load(File(LSSMP.INSTANCE.dataFolder.absolutePath + "/heartRecipe.yml"))
        LSSMP.INSTANCE.removeHeartRecipe()
        LSSMP.HEART_ITEM.setName(((LSSMP.INSTANCE.config.getString("items.heart.name")?.color()
            ?: (ChatColor.RED + "❤ " + ChatColor.WHITE + "Extra heart."))))
            .setLore(LSSMP.INSTANCE.config.getStringList("items.heart.lore").ifEmpty { listOf("Gives you an extra heart!") })
        LSSMP.INSTANCE.server.onlinePlayers.forEach { player ->
            player.inventory.forEach {
                if(it != null && it.getCustomModelData() == LSSMP.HEART_ITEM.getCustomModelData())
                    it.setName(LSSMP.HEART_ITEM.getName()).setCustomModelData(LSSMP.HEART_ITEM.getCustomModelData())
            }
        }
        LSSMP.INSTANCE.addHeartRecipe()
        sender.sendMessage("${ChatColor.GOLD}${if(sender is Player) sender.getTranslation("reload") else "Reloaded!"}")
        return true;
    }
}