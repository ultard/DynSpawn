package me.ultar.dynspawn.manager

import me.ultar.dynspawn.DynSpawn
import me.ultar.dynspawn.api.DynSpawnAPI
import me.ultar.dynspawn.api.event.PlayerDynSpawnEvent
import me.ultar.dynspawn.config.ConfigManager
import me.ultar.dynspawn.model.SpawnRegion
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import kotlin.random.Random

class SpawnManager(
    private val plugin: DynSpawn,
    private val configManager: ConfigManager,
    private val groupManager: GroupManager
) : DynSpawnAPI {

    override fun getRegions(): Collection<SpawnRegion> = configManager.getRegions()

    override fun getEffectiveRegion(player: Player): SpawnRegion? = resolveRegion(player)

    override fun assignRandomRegion(player: Player): SpawnRegion? {
        val regions = configManager.getRegions()
        if (regions.isEmpty()) return null
        val region = regions.random()
        configManager.setPlayerRegion(player.uniqueId, region.id)
        return region
    }

    fun resolveRegion(player: Player): SpawnRegion? {
        groupManager.getGroupByMember(player.uniqueId)?.let { group ->
            configManager.getRegion(group.regionId)?.let { return it }
        }

        configManager.getPlayerRegion(player.uniqueId)?.let { regionId ->
            configManager.getRegion(regionId)?.let { return it }
        }

        return assignRandomRegion(player)
    }

    override fun teleportToSpawn(player: Player): Boolean {
        if (!configManager.hasRegions()) return false

        val region = resolveRegion(player) ?: return false
        val location = getRandomSpawnLocation(region) ?: return false

        val event = PlayerDynSpawnEvent(player, region, location)
        plugin.server.pluginManager.callEvent(event)
        if (event.isCancelled) return false

        return player.teleport(event.spawnLocation)
    }

    fun getRandomSpawnLocation(region: SpawnRegion): Location? {
        val center = region.centerLocation() ?: return null
        val maxRetries = configManager.settings.maxRetries

        repeat(maxRetries) {
            val location = randomLocationInRadius(center, region.radius)
            if (isSafeLocation(location)) {
                return location
            }
        }

        return center.clone().apply { y = center.world.getHighestBlockYAt(blockX, blockZ).toDouble() + 1 }
    }

    fun getRandomSpawnLocation(center: Location, radius: Int): Location? {
        val maxRetries = configManager.settings.maxRetries

        repeat(maxRetries) {
            val location = randomLocationInRadius(center, radius)
            if (isSafeLocation(location)) {
                return location
            }
        }

        return center.clone()
    }

    private fun randomLocationInRadius(center: Location, radius: Int): Location {
        val angle = Random.nextDouble() * 2 * Math.PI
        val distance = Random.nextDouble() * radius
        val x = center.x + distance * kotlin.math.cos(angle)
        val z = center.z + distance * kotlin.math.sin(angle)
        val world = center.world
        val y = world.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble() + 1
        return Location(world, x, y, z, center.yaw, center.pitch)
    }

    private fun isSafeLocation(location: Location): Boolean {
        val world = location.world
        val block = world.getBlockAt(location.blockX, location.blockY - 1, location.blockZ)
        val feet = world.getBlockAt(location.blockX, location.blockY, location.blockZ)
        val head = world.getBlockAt(location.blockX, location.blockY + 1, location.blockZ)

        if (!block.type.isSolid) return false
        if (block.type == Material.LAVA || block.type == Material.WATER) return false
        if (feet.type.isSolid || head.type.isSolid) return false
        if (location.y < world.minHeight + 1) return false

        val below = block.getRelative(BlockFace.DOWN)
        if (below.type == Material.LAVA || below.type == Material.WATER) return false

        return true
    }

    fun getRespawnLocation(player: Player): Location? {
        if (!configManager.hasRegions()) return null
        val region = resolveRegion(player) ?: return null
        val location = getRandomSpawnLocation(region) ?: return null

        val event = PlayerDynSpawnEvent(player, region, location)
        plugin.server.pluginManager.callEvent(event)
        if (event.isCancelled) return null

        return event.spawnLocation
    }
}
