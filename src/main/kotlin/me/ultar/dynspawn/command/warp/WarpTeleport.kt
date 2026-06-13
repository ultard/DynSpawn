package me.ultar.dynspawn.command.warp

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpTeleport : Command("teleport") {
    override val permission = "dynspawn.command.warp.teleport"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.warp.teleport.usage"))
            return
        }

        val name = args[0]
        val warp = plugin().configManager.getWarp(name)
        if (warp == null) {
            sender.sendMessage(msg("messages.warp.teleport.not-found"))
            return
        }

        val config = plugin().configManager.settings
        val location = if (config.warpSpawnAround) {
            val base = warp.toLocation() ?: run {
                sender.sendMessage(msg("messages.warp.teleport.not-found"))
                return
            }
            plugin().spawnManager.getRandomSpawnLocation(base, config.warpSpawnRadius) ?: base
        } else {
            warp.toLocation() ?: run {
                sender.sendMessage(msg("messages.warp.teleport.not-found"))
                return
            }
        }

        player.teleport(location)
        sender.sendMessage(msg("messages.warp.teleport.teleported", "name" to name))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return plugin().configManager.getWarpNames().filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
