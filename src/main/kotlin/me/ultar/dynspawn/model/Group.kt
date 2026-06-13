package me.ultar.dynspawn.model

import java.util.UUID

data class Group(
    val name: String,
    val regionId: String,
    val leader: UUID,
    val members: MutableSet<UUID> = mutableSetOf()
) {
    fun allMembers(): Set<UUID> = members + leader

    fun contains(playerId: UUID): Boolean = leader == playerId || members.contains(playerId)
}
