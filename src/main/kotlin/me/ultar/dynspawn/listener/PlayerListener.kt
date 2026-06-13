package me.ultar.dynspawn.listener

import me.ultar.dynspawn.DynSpawn
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerListener(plugin: DynSpawn) : Listener(plugin) {

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!plugin.configManager.isAutoSpawnEnabled()) return
        if (event.player.hasPlayedBefore()) return

        plugin.server.scheduler.runTask(plugin, Runnable {
            plugin.spawnManager.teleportToSpawn(event.player)
        })
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (!plugin.configManager.isAutoSpawnEnabled()) return

        val location = plugin.spawnManager.getRespawnLocation(event.player) ?: return
        event.respawnLocation = location
    }
}
