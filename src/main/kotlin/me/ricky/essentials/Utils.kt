package me.ricky.essentials

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor

fun color(text: String): String {
  return ChatColor.translateAlternateColorCodes('&', text)
}

fun String.colored(): String = color(this)

fun List<Component>.asLines(): Component {
  return mapIndexed { index, component ->
    if (index == lastIndex) component
    else component.append(Component.text("\n"))
  }.reduce { acc, component ->
    acc.append(component)
  }
}