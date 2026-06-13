package me.ultar.dynspawn.command.spawn

import me.ultar.dynspawn.command.Command
import me.ultar.dynspawn.model.SpawnRegion
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnRegionSet : Command("set") {
    override val permission = "dynspawn.command.spawn.set"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.spawn.region.set.usage"))
            return
        }

        val name = args[0]
        val configManager = plugin().configManager
        val existing = configManager.getRegion(name)

        if (existing == null) {
            sender.sendMessage(msg("messages.spawn.region.set.not-found", "name" to name))
            return
        }

        val location = player.location
        configManager.addRegion(
            SpawnRegion(name, location.world.name, location.x, location.z, existing.radius)
        )
        sender.sendMessage(msg("messages.spawn.region.set.updated", "name" to name))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return plugin().configManager.getRegionIds().filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
