package me.ultar.dynspawn.command.warp

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class WarpCommand : Command("warp") {
    override val subCommands = listOf(
        WarpList(),
        WarpSet(),
        WarpRemove(),
        WarpTeleport()
    )

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (!plugin().configManager.settings.warpEnabled) {
            sender.sendMessage(msg("messages.feature-disabled"))
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.warp.teleport.usage"))
            return
        }

        val subCommand = subCommands.find { it.name.equals(args[0], true) }
        if (subCommand == null) {
            sender.sendMessage(msg("messages.warp.teleport.usage"))
            return
        }

        subCommand.permission?.let { perm ->
            if (!hasPermission(sender, perm)) {
                sender.sendMessage(msg("messages.no-permission"))
                return
            }
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
