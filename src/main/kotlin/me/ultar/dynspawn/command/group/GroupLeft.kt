package me.ultar.dynspawn.command.group

import me.ultar.dynspawn.command.Command
import me.ultar.dynspawn.manager.GroupManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GroupLeft : Command("leave") {
    override val permission = "dynspawn.command.group"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player

        when (val result = plugin().groupManager.leave(player)) {
            GroupManager.LeaveResult.NOT_IN_GROUP ->
                sender.sendMessage(msg("messages.group.not-in-group"))
            GroupManager.LeaveResult.DISBANDED ->
                sender.sendMessage(msg("messages.group.disbanded"))
            GroupManager.LeaveResult.SUCCESS ->
                sender.sendMessage(msg("messages.group.left"))
        }
    }
}
