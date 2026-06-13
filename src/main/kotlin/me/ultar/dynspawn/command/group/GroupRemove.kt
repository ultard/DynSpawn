package me.ultar.dynspawn.command.group

import me.ultar.dynspawn.command.Command
import me.ultar.dynspawn.manager.GroupManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GroupRemove : Command("remove") {
    override val permission = "dynspawn.command.group.remove"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.group.remove.usage"))
            return
        }

        val group = plugin().groupManager.getGroupByMember(player.uniqueId)
        if (group == null) {
            sender.sendMessage(msg("messages.group.not-in-group"))
            return
        }

        if (group.leader != player.uniqueId && !hasPermission(sender, "dynspawn.command.group.remove.other")) {
            sender.sendMessage(msg("messages.no-permission"))
            return
        }

        val target = Bukkit.getPlayerExact(args[0])
        if (target == null) {
            sender.sendMessage(msg("messages.player-not-found"))
            return
        }

        when (val result = plugin().groupManager.remove(group.name, target.uniqueId)) {
            GroupManager.RemoveResult.NOT_FOUND ->
                sender.sendMessage(msg("messages.group.not-found"))
            GroupManager.RemoveResult.NOT_MEMBER ->
                sender.sendMessage(msg("messages.group.remove.not-member"))
            GroupManager.RemoveResult.CANNOT_KICK_LEADER ->
                sender.sendMessage(msg("messages.group.remove.cannot-kick-leader"))
            GroupManager.RemoveResult.SUCCESS ->
                sender.sendMessage(msg("messages.group.remove.kicked", "player" to target.name))
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1 && sender is Player) {
            val group = plugin().groupManager.getGroupByMember(sender.uniqueId) ?: return emptyList()
            return group.members.mapNotNull { Bukkit.getPlayer(it)?.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
