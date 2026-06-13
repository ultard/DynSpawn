package me.ultar.dynspawn.command.spawn

import me.ultar.dynspawn.command.Command
import me.ultar.dynspawn.model.SpawnRegion
import org.bukkit.command.CommandSender

class SpawnRegionRadius : Command("radius") {
    override val permission = "dynspawn.command.spawn.radius"

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.size < 2) {
            sender.sendMessage(msg("messages.spawn.region.radius.usage"))
            return
        }

        val name = args[0]
        val radius = args[1].toIntOrNull()
        if (radius == null || radius <= 0) {
            sender.sendMessage(msg("messages.spawn.region.radius.invalid-radius"))
            return
        }

        val configManager = plugin().configManager
        val existing = configManager.getRegion(name)

        if (existing == null) {
            sender.sendMessage(msg("messages.spawn.region.radius.not-found", "name" to name))
            return
        }

        configManager.addRegion(
            SpawnRegion(name, existing.world, existing.x, existing.z, radius)
        )
        sender.sendMessage(msg("messages.spawn.region.radius.updated", "name" to name, "radius" to radius.toString()))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return plugin().configManager.getRegionIds().filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
