package me.ultar.dynspawn.manager

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader

class MessageManager(private val plugin: JavaPlugin) {

    private var messages: YamlConfiguration = YamlConfiguration()

    fun load(language: String) {
        val enStream = plugin.getResource("lang/en.yml")
            ?: error("Missing lang/en.yml")
        val defaults = YamlConfiguration.loadConfiguration(InputStreamReader(enStream, Charsets.UTF_8))

        val langStream = plugin.getResource("lang/$language.yml") ?: enStream
        messages = YamlConfiguration.loadConfiguration(InputStreamReader(langStream, Charsets.UTF_8))
        messages.setDefaults(defaults)
    }

    fun get(key: String, vararg placeholders: Pair<String, String>): String {
        var message = messages.getString(key) ?: key
        placeholders.forEach { (placeholder, value) ->
            message = message.replace("%$placeholder%", value)
        }
        return message
    }
}
