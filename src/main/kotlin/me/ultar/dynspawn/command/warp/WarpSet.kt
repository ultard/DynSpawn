package me.ultar.dynspawn.command.warp

import me.ultar.dynspawn.command.Command
import me.ultar.dynspawn.model.Warp
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpSet : Command("set") {
    override val permission = "dynspawn.command.warp.set"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.warp.set.usage"))
            return
        }

        val name = args[0]
        if (plugin().configManager.getWarp(name) != null) {
            sender.sendMessage(msg("messages.warp.set.exists"))
            return
        }

        val loc = player.location
        plugin().configManager.addWarp(
            Warp(name, loc.world.name, loc.x, loc.y, loc.z, loc.yaw, loc.pitch)
        )
        sender.sendMessage(msg("messages.warp.set.created", "name" to name))
    }
}
