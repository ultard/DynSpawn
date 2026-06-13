package me.ultar.dynspawn.config

data class PluginSettings(
    val language: String = "en",
    val bstats: Boolean = true,
    val autoSpawn: Boolean = true,
    val maxRetries: Int = 10,
    val defaultRadius: Int = 1000,
    val warpEnabled: Boolean = true,
    val warpSpawnAround: Boolean = true,
    val warpSpawnRadius: Int = 50,
    val groupsEnabled: Boolean = true
)
