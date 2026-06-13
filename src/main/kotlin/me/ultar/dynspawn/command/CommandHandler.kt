package me.ultar.dynspawn.command

import me.ultar.dynspawn.command.general.DynSpawnCommand
import me.ultar.dynspawn.command.group.GroupCommand
import me.ultar.dynspawn.command.spawn.SpawnCommand
import me.ultar.dynspawn.command.warp.WarpCommand
import me.ultar.dynspawn.manager.Cooldown
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CommandHandler(private val plugin: me.ultar.dynspawn.DynSpawn) : CommandExecutor, TabCompleter {
    private val commands = mutableMapOf<String, me.ultar.dynspawn.command.Command>()
    private val cooldown = Cooldown()

    init {
        registerCommand(DynSpawnCommand())
        registerCommand(SpawnCommand())
        registerCommand(WarpCommand())
        registerCommand(GroupCommand())
    }

    fun register() {
        REGISTERED_COMMANDS.forEach { name ->
            plugin.getCommand(name)?.apply {
                setExecutor(this@CommandHandler)
                tabCompleter = this@CommandHandler
            } ?: plugin.logger.warning("Command '/$name' is not defined in plugin.yml")
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        dispatch(sender, command.name.lowercase(), args)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> = autoComplete(sender, command.name.lowercase(), args)

    private fun registerCommand(cmd: me.ultar.dynspawn.command.Command, parentName: String? = null) {
        val name = if (parentName != null) "$parentName.${cmd.name}" else cmd.name
        commands[name] = cmd
        cmd.aliases.forEach { commands["$parentName.$it"] = cmd }
        cmd.subCommands.forEach { registerCommand(it, name) }
    }

    fun dispatch(sender: CommandSender, baseCommand: String, args: Array<out String>) {
        val fullCommand = buildCommandPath(baseCommand, args)
        var command = commands[fullCommand]

        if (command == null) {
            command = commands[baseCommand]
            if (command == null) {
                sender.sendMessage(plugin.msg("messages.dynspawn.help-header"))
                return
            }
            if (args.isEmpty()) {
                command.execute(sender, emptyArray())
                return
            }
        }

        if (command.isDisabled) return

        command.permission?.let { perm ->
            if (!sender.hasPermission(perm)) {
                sender.sendMessage(plugin.msg("messages.no-permission"))
                return
            }
        }

        if (command.isPlayerOnly && sender !is Player) {
            sender.sendMessage(plugin.msg("messages.players-only"))
            return
        }

        if (command.isCooldownEnabled && sender is Player) {
            if (cooldown.isOnCooldown(sender.uniqueId, command.name)) {
                val remaining = cooldown.getRemainingTime(sender.uniqueId, command.name)
                sender.sendMessage(plugin.msg("messages.cooldown", "seconds" to remaining.toString()))
                return
            }
            cooldown.setCooldown(sender.uniqueId, command.name, command.cooldownTime)
        }

        val depth = fullCommand.count { it == '.' }
        val remainingArgs = args.drop(depth).toTypedArray()
        command.execute(sender, remainingArgs)
    }

    private fun buildCommandPath(baseCommand: String, args: Array<out String>): String {
        val commandParts = mutableListOf(baseCommand)
        for (arg in args) {
            val nextPath = commandParts.last() + "." + arg.lowercase()
            if (!commands.containsKey(nextPath)) break
            commandParts.add(nextPath)
        }
        return commandParts.last()
    }

    fun autoComplete(sender: CommandSender, command: String, args: Array<out String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val fullPath = buildCommandPath(command, args.copyOfRange(0, args.size - 1))
        val current = commands[fullPath] ?: commands[command] ?: return emptyList()
        val lastArg = args.last()

        val subNames = current.subCommands
            .filter { !it.isHidden }
            .filter { it.permission == null || sender.hasPermission(it.permission!!) }
            .map { it.name }
            .filter { it.startsWith(lastArg, ignoreCase = true) }

        if (subNames.isNotEmpty() && args.size <= fullPath.count { it == '.' }) {
            return subNames
        }

        return current.tabComplete(sender, Array(args.size) { args[it] })
            .filter { it.startsWith(lastArg, ignoreCase = true) }
    }

    companion object {
        private val REGISTERED_COMMANDS = listOf("dynspawn", "spawn", "warp", "group")
    }
}
