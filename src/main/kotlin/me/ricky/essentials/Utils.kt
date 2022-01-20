package me.ricky.essentials

import org.bukkit.ChatColor

fun color(text: String): String {
  return ChatColor.translateAlternateColorCodes('&', text)
}

fun String.colored(): String = color(this)