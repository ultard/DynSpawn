package me.ultar.dynspawn.command.group

import me.ultar.dynspawn.command.Command
import me.ultar.dynspawn.manager.GroupManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GroupJoin : Command("join") {
    override val permission = "dynspawn.command.group.create"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.group.join.usage"))
            return
        }

        when (val result = plugin().groupManager.join(args[0], player)) {
            GroupManager.JoinResult.DISABLED ->
                sender.sendMessage(msg("messages.group.disabled"))
            GroupManager.JoinResult.NOT_FOUND ->
                sender.sendMessage(msg("messages.group.not-found"))
            GroupManager.JoinResult.ALREADY_IN_GROUP ->
                sender.sendMessage(msg("messages.group.already-in-group"))
            GroupManager.JoinResult.ALREADY_IN_OTHER ->
                sender.sendMessage(msg("messages.group.already-in-other"))
            GroupManager.JoinResult.SUCCESS ->
                sender.sendMessage(msg("messages.group.joined", "name" to args[0]))
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return plugin().groupManager.getGroupNames().filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
