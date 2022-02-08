package me.ikevoodoo.lssmp.utils

import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemUtils {

    companion object {
        fun ItemStack.entity(): ItemEntity {
            return ItemEntity(this)
        }

        fun ItemStack.setName(name: String): ItemStack {
            val itemMeta = this.itemMeta
            itemMeta?.setDisplayName(name)
            this.itemMeta = itemMeta
            return this
        }

        fun ItemStack.getName(): String {
            val itemMeta = this.itemMeta
            return itemMeta?.displayName ?: ""
        }

        fun ItemStack.setIdentifier(key: NamespacedKey): ItemStack {
            val itemMeta = this.itemMeta
            itemMeta?.persistentDataContainer?.set(key, PersistentDataType.BYTE, 0)
            this.itemMeta = itemMeta
            return this
        }

        fun ItemStack.setCustomModelData(modelData: Int): ItemStack {
            val itemMeta = this.itemMeta
            itemMeta?.setCustomModelData(modelData)
            this.itemMeta = itemMeta
            return this
        }

        fun ItemStack.getCustomModelData(): Int {
            val itemMeta = this.itemMeta
            return if(itemMeta != null && itemMeta.hasCustomModelData()) itemMeta.customModelData else 0
        }

        fun ItemStack.setLore(lore: List<String>): ItemStack {
            val itemMeta = this.itemMeta
            itemMeta?.lore = lore.map { ChatColor.translateAlternateColorCodes('&', it) }
            this.itemMeta = itemMeta
            return this
        }
    }

}