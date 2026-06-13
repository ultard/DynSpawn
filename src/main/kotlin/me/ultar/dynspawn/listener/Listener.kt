package me.ultar.dynspawn.listener

import me.ultar.dynspawn.DynSpawn

abstract class Listener(val plugin: DynSpawn) : org.bukkit.event.Listener {
    init {
        register()
    }

    fun register() {
        this.plugin.server.pluginManager.registerEvents(this, plugin)
    }
}