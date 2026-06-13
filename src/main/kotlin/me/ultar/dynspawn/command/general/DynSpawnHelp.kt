package me.ultar.dynspawn.command.general

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class DynSpawnHelp : Command("help") {
    override fun execute(sender: CommandSender, args: Array<String>) {
        sender.sendMessage(msg("messages.dynspawn.help-header"))
        HELP_LINES.forEach { line ->
            sender.sendMessage(msg("messages.dynspawn.help-line", "command" to line))
        }
    }

    companion object {
        private val HELP_LINES = listOf(
            "dynspawn help|reload|version",
            "spawn region create|set|radius|remove|list",
            "warp list|set|remove|teleport",
            "group create|join|leave|remove"
        )
    }
}
