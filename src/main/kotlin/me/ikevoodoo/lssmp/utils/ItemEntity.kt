package me.ikevoodoo.lssmp.utils

import org.bukkit.Location
import org.bukkit.inventory.ItemStack

class ItemEntity(item: ItemStack) {

    private var gravity = false
    private var pos = Location(null, 0.0, 0.0, 0.0)

    private val itemStack = item

    fun drop() {
        pos.world?.dropItem(pos, itemStack)?.setGravity(gravity)
    }

    fun setGravity(gravity: Boolean): ItemEntity {
        this.gravity = gravity
        return this
    }

    fun setPosition(location: Location): ItemEntity {
        pos = location
        return this
    }



}