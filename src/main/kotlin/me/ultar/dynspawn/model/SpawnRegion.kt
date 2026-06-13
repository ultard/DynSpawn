package me.ultar.dynspawn.model

import org.bukkit.Bukkit
import org.bukkit.Location

data class SpawnRegion(
    val id: String,
    val world: String,
    val x: Double,
    val z: Double,
    val radius: Int
) {
    fun centerLocation(): Location? {
        val bukkitWorld = Bukkit.getWorld(world) ?: return null
        return Location(bukkitWorld, x, bukkitWorld.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble(), z)
    }
}
