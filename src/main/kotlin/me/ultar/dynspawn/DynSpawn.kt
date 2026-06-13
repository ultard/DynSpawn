package me.ultar.dynspawn

import me.ultar.dynspawn.api.DynSpawnAPI
import me.ultar.dynspawn.command.CommandHandler
import me.ultar.dynspawn.config.ConfigManager
import me.ultar.dynspawn.listener.PlayerListener
import me.ultar.dynspawn.manager.GroupManager
import me.ultar.dynspawn.manager.MessageManager
import me.ultar.dynspawn.manager.SpawnManager
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class DynSpawn : JavaPlugin() {

    lateinit var configManager: ConfigManager
        private set
    lateinit var messageManager: MessageManager
        private set
    lateinit var groupManager: GroupManager
        private set
    lateinit var spawnManager: SpawnManager
        private set
    lateinit var commandHandler: CommandHandler
        private set

    override fun onEnable() {
        instance = this

        configManager = ConfigManager(this)
        configManager.load()

        messageManager = MessageManager(this)
        messageManager.load(configManager.settings.language)

        groupManager = GroupManager(configManager)
        spawnManager = SpawnManager(this, configManager, groupManager)
        commandHandler = CommandHandler(this)
        commandHandler.register()

        server.servicesManager.register(DynSpawnAPI::class.java, spawnManager, this, ServicePriority.Normal)

        PlayerListener(this)

        if (configManager.settings.bstats) {
            try {
                Metrics(this, 26274)
            } catch (ex: Exception) {
                logger.warning("Failed to start bStats metrics: ${ex.message}")
            }
        }

        logger.info("DynSpawn enabled.")
    }

    override fun onDisable() {
        server.servicesManager.unregisterAll(this)
    }

    fun msg(key: String, vararg placeholders: Pair<String, String>): String =
        messageManager.get(key, *placeholders)

    companion object {
        lateinit var instance: DynSpawn
            private set
    }
}
