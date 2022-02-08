package me.ikevoodoo.lssmp.utils

import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.isEliminated
import org.bukkit.Bukkit

class CompletionUtils {

    companion object {
        fun getPlayerNames(includeOffline: Boolean = true, includeEliminated: Boolean = false): MutableList<String> {
            val names = mutableListOf<String>()
            for(player in Bukkit.getOnlinePlayers()) {
                if(!includeEliminated && player.isEliminated()) continue
                names.add(player.name)
            }

            if(includeOffline) {
                for(player in Bukkit.getOfflinePlayers()) {
                    if(!includeEliminated && player.uniqueId.isEliminated()) continue
                    player.name?.let { names.add(it) }
                }
            }

            return names
        }
    }

}