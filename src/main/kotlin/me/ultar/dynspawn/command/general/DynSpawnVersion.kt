package me.ultar.dynspawn.command.general

import me.ultar.dynspawn.DynSpawn
import me.ultar.dynspawn.command.Command
import org.bukkit.command.CommandSender

class DynSpawnVersion : Command("version") {
    override fun execute(sender: CommandSender, args: Array<String>) {
        val plugin = sender.server.pluginManager.getPlugin("DynSpawn") as DynSpawn
        sender.sendMessage("DynSpawn version: ${plugin.pluginMeta.version}")
    }
}