package me.ultar.dynspawn.model

import org.bukkit.Bukkit
import org.bukkit.Location

data class Warp(
    val name: String,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0f,
    val pitch: Float = 0f
) {
    fun toLocation(): Location? {
        val bukkitWorld = Bukkit.getWorld(world) ?: return null
        return Location(bukkitWorld, x, y, z, yaw, pitch)
    }
}
