package me.ultar.dynspawn.command.warp

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class WarpRemove : Command("remove") {
    override val permission = "dynspawn.command.warp.remove"

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.warp.remove.usage"))
            return
        }

        val name = args[0]
        if (!plugin().configManager.removeWarp(name)) {
            sender.sendMessage(msg("messages.warp.remove.not-found"))
            return
        }

        sender.sendMessage(msg("messages.warp.remove.removed", "name" to name))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return plugin().configManager.getWarpNames().filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
