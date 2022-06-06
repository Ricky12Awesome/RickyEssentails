package me.ricky.essentials

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer

fun color(text: String): String {
  return ChatColor.translateAlternateColorCodes('&', text)
}

fun String.colored(): String = color(this)

fun arrayOfOfflinePlayersNames(): Array<out String?> {
  return Bukkit.getOfflinePlayers().map(OfflinePlayer::getName).toTypedArray()
}

fun List<Component>.asLines(): Component {
  return mapIndexed { index, component ->
    if (index == lastIndex) component
    else component.append(Component.text("\n"))
  }.reduce { acc, component ->
    acc.append(component)
  }
}