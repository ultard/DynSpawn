package me.ultar.dynspawn.command.group

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GroupCreate : Command("create") {
    override val permission = "dynspawn.command.group.create"
    override val isPlayerOnly = true

    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.group.create.usage"))
            return
        }

        if (!plugin().configManager.settings.groupsEnabled) {
            sender.sendMessage(msg("messages.group.disabled"))
            return
        }

        val name = args[0]
        val configManager = plugin().configManager
        val groupManager = plugin().groupManager

        when {
            configManager.getGroup(name) != null ->
                sender.sendMessage(msg("messages.group.already-exists"))
            groupManager.getGroupByMember(player.uniqueId) != null ->
                sender.sendMessage(msg("messages.group.already-in-other"))
            !configManager.hasRegions() ->
                sender.sendMessage(msg("messages.spawn.no-regions"))
            groupManager.create(name, player) != null ->
                sender.sendMessage(msg("messages.group.created", "name" to name))
            else ->
                sender.sendMessage(msg("messages.group.already-exists"))
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> = emptyList()
}
