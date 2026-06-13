package me.ultar.dynspawn.command.warp

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class WarpList : Command("list") {
    override val permission = "dynspawn.command.warp.list"

    override fun execute(sender: CommandSender, args: Array<String>) {
        val warps = plugin().configManager.getWarps()
        if (warps.isEmpty()) {
            sender.sendMessage(msg("messages.warp.list.no-warps"))
            return
        }

        sender.sendMessage(msg("messages.warp.list.title"))
        warps.forEach { warp ->
            sender.sendMessage(msg("messages.warp.list.entry", "name" to warp.name))
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> = emptyList()
}
