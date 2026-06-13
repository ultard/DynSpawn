package me.ultar.dynspawn.command

import me.ultar.dynspawn.DynSpawn
import org.bukkit.command.CommandSender

abstract class Command(val name: String) {
    open val aliases: List<String> = emptyList()
    open val subCommands: List<Command> = emptyList()

    open val isHidden = false
    open val isDisabled = false
    open val isPlayerOnly = false
    open val permission: String? = null

    open val isCooldownEnabled = false
    open val cooldownTime = 0

    abstract fun execute(sender: CommandSender, args: Array<String>)

    protected fun plugin(): DynSpawn = DynSpawn.instance

    protected fun msg(key: String, vararg placeholders: Pair<String, String>): String =
        plugin().msg(key, *placeholders)

    protected fun hasPermission(sender: CommandSender, permission: String): Boolean =
        sender.hasPermission(permission)

    open fun tabComplete(sender: CommandSender, args: Array<String>): List<String> = emptyList()
}
