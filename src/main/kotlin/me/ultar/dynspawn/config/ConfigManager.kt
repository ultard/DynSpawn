package me.ultar.dynspawn.config

import me.ultar.dynspawn.model.Group
import me.ultar.dynspawn.model.SpawnRegion
import me.ultar.dynspawn.model.Warp
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class ConfigManager(private val plugin: JavaPlugin) {

    private val regionsFile = File(plugin.dataFolder, "regions.yml")
    private val playersFile = File(plugin.dataFolder, "players.yml")
    private val groupsFile = File(plugin.dataFolder, "groups.yml")
    private val warpsFile = File(plugin.dataFolder, "warps.yml")

    var settings: PluginSettings = PluginSettings()
        private set

    private val regions = mutableMapOf<String, SpawnRegion>()
    private val playerBindings = mutableMapOf<UUID, String>()
    private val groups = mutableMapOf<String, Group>()
    private val warps = mutableMapOf<String, Warp>()

    fun load() {
        plugin.dataFolder.mkdirs()
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        loadSettings()
        loadRegions()
        loadPlayers()
        loadGroups()
        loadWarps()
    }

    fun reload() {
        load()
    }

    private fun loadSettings() {
        val config = plugin.config
        settings = PluginSettings(
            language = config.getString("plugin.language", "en") ?: "en",
            bstats = config.getBoolean("plugin.bstats", true),
            autoSpawn = config.getBoolean("spawn.auto-spawn", true),
            maxRetries = config.getInt("spawn.max-retries", 10),
            defaultRadius = config.getInt("spawn.default-radius", 1000),
            warpEnabled = config.getBoolean("warp.enabled", true),
            warpSpawnAround = config.getBoolean("warp.spawn_around", true),
            warpSpawnRadius = config.getInt("warp.spawn_radius", 50),
            groupsEnabled = config.getBoolean("groups.enabled", true)
        )
    }

    private fun loadRegions() {
        regions.clear()
        if (!regionsFile.exists()) {
            plugin.saveResource("regions.yml", false)
        }
        val yaml = YamlConfiguration.loadConfiguration(regionsFile)
        for (key in yaml.getKeys(false)) {
            val section = yaml.getConfigurationSection(key) ?: continue
            regions[key] = SpawnRegion(
                id = key,
                world = section.getString("world", "world") ?: "world",
                x = section.getDouble("x", 0.0),
                z = section.getDouble("z", 0.0),
                radius = section.getInt("radius", settings.defaultRadius)
            )
        }
    }

    private fun loadPlayers() {
        playerBindings.clear()
        if (!playersFile.exists()) return
        val yaml = YamlConfiguration.loadConfiguration(playersFile)
        for (key in yaml.getKeys(false)) {
            val uuid = runCatching { UUID.fromString(key) }.getOrNull() ?: continue
            val regionId = yaml.getString(key) ?: continue
            playerBindings[uuid] = regionId
        }
    }

    private fun loadGroups() {
        groups.clear()
        if (!groupsFile.exists()) return
        val yaml = YamlConfiguration.loadConfiguration(groupsFile)
        for (key in yaml.getKeys(false)) {
            val section = yaml.getConfigurationSection(key) ?: continue
            val leader = runCatching { UUID.fromString(section.getString("leader")) }.getOrNull() ?: continue
            val regionId = section.getString("region") ?: continue
            val members = section.getStringList("members")
                .mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }
                .toMutableSet()
            groups[key.lowercase()] = Group(key, regionId, leader, members)
        }
    }

    private fun loadWarps() {
        warps.clear()
        if (!warpsFile.exists()) return
        val yaml = YamlConfiguration.loadConfiguration(warpsFile)
        for (key in yaml.getKeys(false)) {
            val section = yaml.getConfigurationSection(key) ?: continue
            warps[key.lowercase()] = Warp(
                name = key,
                world = section.getString("world", "world") ?: "world",
                x = section.getDouble("x", 0.0),
                y = section.getDouble("y", 64.0),
                z = section.getDouble("z", 0.0),
                yaw = section.getDouble("yaw", 0.0).toFloat(),
                pitch = section.getDouble("pitch", 0.0).toFloat()
            )
        }
    }

    fun saveRegions() {
        val yaml = YamlConfiguration()
        regions.values.forEach { region ->
            yaml.set("${region.id}.world", region.world)
            yaml.set("${region.id}.x", region.x)
            yaml.set("${region.id}.z", region.z)
            yaml.set("${region.id}.radius", region.radius)
        }
        yaml.save(regionsFile)
    }

    fun savePlayers() {
        val yaml = YamlConfiguration()
        playerBindings.forEach { (uuid, regionId) ->
            yaml.set(uuid.toString(), regionId)
        }
        yaml.save(playersFile)
    }

    fun saveGroups() {
        val yaml = YamlConfiguration()
        groups.values.forEach { group ->
            yaml.set("${group.name}.leader", group.leader.toString())
            yaml.set("${group.name}.region", group.regionId)
            yaml.set("${group.name}.members", group.members.map { it.toString() })
        }
        yaml.save(groupsFile)
    }

    fun saveWarps() {
        val yaml = YamlConfiguration()
        warps.values.forEach { warp ->
            yaml.set("${warp.name}.world", warp.world)
            yaml.set("${warp.name}.x", warp.x)
            yaml.set("${warp.name}.y", warp.y)
            yaml.set("${warp.name}.z", warp.z)
            yaml.set("${warp.name}.yaw", warp.yaw)
            yaml.set("${warp.name}.pitch", warp.pitch)
        }
        yaml.save(warpsFile)
    }

    fun isAutoSpawnEnabled(): Boolean = settings.autoSpawn

    fun getRegions(): Collection<SpawnRegion> = regions.values

    fun getRegion(id: String): SpawnRegion? = regions[id]

    fun getRegionIds(): List<String> = regions.keys.sorted()

    fun addRegion(region: SpawnRegion) {
        regions[region.id] = region
        saveRegions()
    }

    fun removeRegion(id: String): Boolean {
        if (regions.remove(id) == null) return false
        saveRegions()
        return true
    }

    fun getPlayerRegion(playerId: UUID): String? = playerBindings[playerId]

    fun setPlayerRegion(playerId: UUID, regionId: String) {
        playerBindings[playerId] = regionId
        savePlayers()
    }

    fun getGroups(): Collection<Group> = groups.values

    fun getGroup(name: String): Group? = groups[name.lowercase()]

    fun getGroupNames(): List<String> = groups.keys.sorted()

    fun getGroupByMember(playerId: UUID): Group? =
        groups.values.find { it.contains(playerId) }

    fun addGroup(group: Group) {
        groups[group.name.lowercase()] = group
        saveGroups()
    }

    fun removeGroup(name: String): Boolean {
        if (groups.remove(name.lowercase()) == null) return false
        saveGroups()
        return true
    }

    fun updateGroup(group: Group) {
        groups[group.name.lowercase()] = group
        saveGroups()
    }

    fun getWarps(): Collection<Warp> = warps.values

    fun getWarp(name: String): Warp? = warps[name.lowercase()]

    fun getWarpNames(): List<String> = warps.keys.sorted()

    fun addWarp(warp: Warp) {
        warps[warp.name.lowercase()] = warp
        saveWarps()
    }

    fun removeWarp(name: String): Boolean {
        if (warps.remove(name.lowercase()) == null) return false
        saveWarps()
        return true
    }

    fun hasRegions(): Boolean = regions.isNotEmpty()
}
