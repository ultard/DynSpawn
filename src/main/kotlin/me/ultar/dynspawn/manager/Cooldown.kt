package me.ultar.dynspawn.manager

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class Cooldown {
    private val cooldowns = ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>>()

    fun setCooldown(playerUUID: UUID, commandName: String, seconds: Int) {
        val playerCooldowns = cooldowns.computeIfAbsent(playerUUID) { ConcurrentHashMap() }
        playerCooldowns[commandName] = System.currentTimeMillis() + (seconds * 1000)
    }

    fun isOnCooldown(playerUUID: UUID, commandName: String): Boolean {
        val playerCooldowns = cooldowns[playerUUID] ?: return false
        val cooldownTime = playerCooldowns[commandName] ?: return false
        if (cooldownTime <= System.currentTimeMillis()) {
            playerCooldowns.remove(commandName)
            return false
        }
        return true
    }

    fun getRemainingTime(playerUUID: UUID, commandName: String): Long {
        val playerCooldowns = cooldowns[playerUUID] ?: return 0
        val cooldownTime = playerCooldowns[commandName] ?: return 0
        val remaining = cooldownTime - System.currentTimeMillis()
        return if (remaining > 0) remaining / 1000 else 0
    }
}