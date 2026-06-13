package me.ultar.dynspawn.api

import me.ultar.dynspawn.model.SpawnRegion
import org.bukkit.entity.Player

interface DynSpawnAPI {
    fun teleportToSpawn(player: Player): Boolean
    fun getEffectiveRegion(player: Player): SpawnRegion?
    fun assignRandomRegion(player: Player): SpawnRegion?
    fun getRegions(): Collection<SpawnRegion>
}
