package me.ricky.essentials

import org.bukkit.OfflinePlayer
import org.bukkit.Statistic

class PlayerStats(
  val online: Boolean,
  val lastLogin: Long,
  val lastSeen: Long,
  val deaths: Int,
  val mobKills: Int,
  val playerKills: Int,
) {
  constructor(player: OfflinePlayer): this(
    online = player.isOnline,
    lastLogin = player.lastLogin,
    lastSeen = player.lastSeen,
    deaths = player.getStatistic(Statistic.DEATHS),
    mobKills = player.getStatistic(Statistic.MOB_KILLS),
    playerKills = player.getStatistic(Statistic.PLAYER_KILLS)
  )
}