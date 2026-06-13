package me.ultar.dynspawn.command.spawn

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class SpawnRegionList : Command("list") {
    override val permission = "dynspawn.command.spawn"

    override fun execute(sender: CommandSender, args: Array<String>) {
        val regions = plugin().configManager.getRegions()
        if (regions.isEmpty()) {
            sender.sendMessage(msg("messages.spawn.region.list.empty"))
            return
        }

        sender.sendMessage(msg("messages.spawn.region.list.header"))
        regions.forEach { region ->
            sender.sendMessage(
                msg(
                    "messages.spawn.region.list.entry",
                    "name" to region.id,
                    "world" to region.world,
                    "x" to region.x.toInt().toString(),
                    "z" to region.z.toInt().toString(),
                    "radius" to region.radius.toString()
                )
            )
        }
    }
}
