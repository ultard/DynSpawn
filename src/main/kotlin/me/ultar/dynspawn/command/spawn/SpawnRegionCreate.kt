package me.ultar.dynspawn.command.spawn

import me.ultar.dynspawn.command.Command
import me.ultar.dynspawn.model.SpawnRegion
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnRegionCreate : Command("create") {
    override val permission = "dynspawn.command.spawn.set"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.spawn.region.create.usage"))
            return
        }

        val name = args[0]
        val configManager = plugin().configManager

        if (configManager.getRegion(name) != null) {
            sender.sendMessage(msg("messages.spawn.region.create.exists", "name" to name))
            return
        }

        val radius = args.getOrNull(1)?.toIntOrNull() ?: configManager.settings.defaultRadius
        if (radius <= 0) {
            sender.sendMessage(msg("messages.spawn.region.radius.invalid-radius"))
            return
        }

        val location = player.location
        configManager.addRegion(
            SpawnRegion(name, location.world.name, location.x, location.z, radius)
        )
        sender.sendMessage(msg("messages.spawn.region.create.created", "name" to name, "radius" to radius.toString()))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> = emptyList()
}
