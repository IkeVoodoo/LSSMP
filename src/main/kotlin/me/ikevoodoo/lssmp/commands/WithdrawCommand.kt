package me.ikevoodoo.lssmp.commands

import me.ikevoodoo.lssmp.LSSMP
import me.ikevoodoo.lssmp.TranslationManager.Companion.getTranslation
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.decreaseMaxHealth
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.isEliminated
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.tryEliminate
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.entity
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*
import java.util.stream.IntStream

class WithdrawCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("lssmp.withdraw")) {
            sender.getTranslation("commands.withdraw.no-perms")?.let { sender.sendMessage(it) }
            return true
        }

        if (sender is Player) {
            val inventory: Inventory = sender.inventory
            val items = inventory.contents
            val hasGiveParameter = IntStream.range(0, args.size).anyMatch { i: Int ->
                args[i].lowercase(Locale.ROOT) == "-g" || args[i].lowercase(Locale.ROOT) == "--give"
            } && sender.hasPermission("lssmp.withdraw.give")

            if (!hasGiveParameter) {
                sender.decreaseMaxHealth(true)
                sender.tryEliminate(true)
                if(sender.isEliminated()) {
                    return true;
                }
            }

            if (sender.isEliminated() || !inventory.isEmpty && IntStream.range(0, items.size).noneMatch { i: Int -> items[i] == null }) {
                var dest = sender.eyeLocation.add(0.0, 1.0, 0.0)
                if (dest.block.type.isSolid) dest = sender.eyeLocation
                LSSMP.HEART_ITEM.entity().setGravity(false).setPosition(dest).drop()
                sender.world.dropItem(dest, LSSMP.HEART_ITEM).setGravity(false)
            } else inventory.addItem(LSSMP.HEART_ITEM)

            sender.getTranslation("commands.withdraw.message")?.let { sender.sendMessage(it.replace("%amount%", 1.toString())) }
        }
        return true
    }
}