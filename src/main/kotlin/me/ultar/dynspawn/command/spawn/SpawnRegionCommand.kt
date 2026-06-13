package me.ultar.dynspawn.command.spawn

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class SpawnRegionCommand : Command("region") {
    override val permission = "dynspawn.command.spawn"
    override val subCommands = listOf(
        SpawnRegionCreate(),
        SpawnRegionSet(),
        SpawnRegionRadius(),
        SpawnRegionRemove(),
        SpawnRegionList()
    )

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.spawn.region.usage"))
            return
        }

        val subCommand = subCommands.find { it.name.equals(args[0], true) }
        if (subCommand == null) {
            sender.sendMessage(msg("messages.spawn.region.usage"))
            return
        }

        subCommand.execute(sender, args.sliceArray(1 until args.size))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return subCommands.map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        val sub = subCommands.find { it.name.equals(args[0], ignoreCase = true) } ?: return emptyList()
        return sub.tabComplete(sender, args.drop(1).toTypedArray())
    }
}
