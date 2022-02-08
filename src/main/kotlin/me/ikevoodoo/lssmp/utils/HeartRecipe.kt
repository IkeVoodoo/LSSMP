package me.ikevoodoo.lssmp.utils

import org.bukkit.Material

data class HeartRecipe(val enabled: Boolean, val outputAmount: Int, val shaped: Boolean, val slots: Array<Material>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HeartRecipe

        if (enabled != other.enabled) return false
        if (outputAmount != other.outputAmount) return false
        if (shaped != other.shaped) return false
        if (!slots.contentEquals(other.slots)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + outputAmount
        result = 31 * result + shaped.hashCode()
        result = 31 * result + slots.contentHashCode()
        return result
    }
}
