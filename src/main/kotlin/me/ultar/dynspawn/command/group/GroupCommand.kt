package me.ultar.dynspawn.command.group

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class GroupCommand : Command("group") {
    override val permission = "dynspawn.command.group"
    override val isPlayerOnly = true
    override val subCommands = listOf(
        GroupCreate(),
        GroupJoin(),
        GroupLeft(),
        GroupRemove()
    )

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(msg("messages.group.create.usage"))
            return
        }

        val subCommand = subCommands.find { it.name.equals(args[0], true) }
        if (subCommand == null) {
            sender.sendMessage(msg("messages.group.create.usage"))
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
