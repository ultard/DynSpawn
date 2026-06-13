package me.ultar.dynspawn.command.general

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class DynSpawnCommand : Command("dynspawn") {
    override val permission = "dynspawn.admin"
    override val subCommands = listOf(
        DynSpawnHelp(),
        DynSpawnReload(),
        DynSpawnVersion()
    )

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            DynSpawnHelp().execute(sender, args)
            return
        }

        val subCommand = subCommands.find { it.name.equals(args[0], true) }
        if (subCommand == null) {
            DynSpawnHelp().execute(sender, args)
            return
        }

        subCommand.execute(sender, args.sliceArray(1 until args.size))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.size == 1) {
            return subCommands.map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
