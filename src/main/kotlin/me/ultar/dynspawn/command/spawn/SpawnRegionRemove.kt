package me.ultar.dynspawn.command.spawn

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class SpawnRegionRemove : Command("remove") {
    override val permission = "dynspawn.command.spawn.set"

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.spawn.region.remove.usage"))
            return
        }

        val name = args[0]
        if (!plugin().configManager.removeRegion(name)) {
            sender.sendMessage(msg("messages.spawn.region.remove.not-found", "name" to name))
            return
        }

        sender.sendMessage(msg("messages.spawn.region.remove.removed", "name" to name))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return plugin().configManager.getRegionIds().filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
