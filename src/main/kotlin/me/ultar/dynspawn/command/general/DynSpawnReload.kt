package me.ultar.dynspawn.command.general

import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class DynSpawnReload : Command("reload") {
    override val permission = "dynspawn.admin"

    override fun execute(sender: CommandSender, args: Array<String>) {
        val plugin = plugin()
        plugin.configManager.reload()
        plugin.messageManager.load(plugin.configManager.settings.language)
        sender.sendMessage(plugin.msg("messages.dynspawn.reload"))
    }
}
