package me.ultar.dynspawn.manager

import me.ultar.dynspawn.config.ConfigManager
import me.ultar.dynspawn.model.Group
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.random.Random

class GroupManager(private val configManager: ConfigManager) {

    fun create(name: String, leader: Player): Group? {
        if (!configManager.settings.groupsEnabled) return null
        if (configManager.getGroup(name) != null) return null
        if (getGroupByMember(leader.uniqueId) != null) return null

        val regions = configManager.getRegions()
        if (regions.isEmpty()) return null

        val region = regions.random()
        val group = Group(name, region.id, leader.uniqueId, mutableSetOf())
        configManager.addGroup(group)
        return group
    }

    fun join(name: String, player: Player): JoinResult {
        if (!configManager.settings.groupsEnabled) return JoinResult.DISABLED
        val group = configManager.getGroup(name) ?: return JoinResult.NOT_FOUND
        if (group.contains(player.uniqueId)) return JoinResult.ALREADY_IN_GROUP
        if (getGroupByMember(player.uniqueId) != null) return JoinResult.ALREADY_IN_OTHER

        group.members.add(player.uniqueId)
        configManager.updateGroup(group)
        return JoinResult.SUCCESS
    }

    fun leave(player: Player): LeaveResult {
        val group = getGroupByMember(player.uniqueId) ?: return LeaveResult.NOT_IN_GROUP

        if (group.leader == player.uniqueId) {
            configManager.removeGroup(group.name)
            return LeaveResult.DISBANDED
        }

        group.members.remove(player.uniqueId)
        configManager.updateGroup(group)
        return LeaveResult.SUCCESS
    }

    fun remove(groupName: String, targetId: UUID): RemoveResult {
        val group = configManager.getGroup(groupName) ?: return RemoveResult.NOT_FOUND
        if (group.leader == targetId) return RemoveResult.CANNOT_KICK_LEADER
        if (!group.members.remove(targetId)) return RemoveResult.NOT_MEMBER

        configManager.updateGroup(group)
        return RemoveResult.SUCCESS
    }

    fun getGroupByMember(playerId: UUID): Group? = configManager.getGroupByMember(playerId)

    fun getGroupNames(): List<String> = configManager.getGroupNames()

    enum class JoinResult { SUCCESS, NOT_FOUND, ALREADY_IN_GROUP, ALREADY_IN_OTHER, DISABLED }
    enum class LeaveResult { SUCCESS, NOT_IN_GROUP, DISBANDED }
    enum class RemoveResult { SUCCESS, NOT_FOUND, NOT_MEMBER, CANNOT_KICK_LEADER }
}
