package me.ricky.essentials

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ChatColorArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import kotlin.time.Duration.Companion.seconds

fun toggleVanillaChatCommand(plugin: Plugin) {
  CommandAPICommand("togglevanillachat")
    .executesPlayer(PlayerCommandExecutor { player, args ->
      val useVanillaChatValue = player?.persistentDataContainer?.getOrDefault(
        NamespacedKey(plugin, "useVanillaChat"),
        PersistentDataType.INTEGER, 1
      ) ?: 0

      val useVanillaChat = useVanillaChatValue > 0
      val value = if (!useVanillaChat) 1 else 0
      player.persistentDataContainer.set(NamespacedKey(plugin, "useVanillaChat"), PersistentDataType.INTEGER, value)

      if (value > 0) {
        player.sendMessage("&eChat is now vanilla style, specifically made for Matt and Tom".colored())
      } else {
        player.sendMessage("&eChat is now fancy style".colored())
      }
    })
    .register()
}

fun nameColorCommand(plugin: Plugin) {
  val blacklist = listOf(
    ChatColor.MAGIC,
    ChatColor.BOLD,
    ChatColor.STRIKETHROUGH,
    ChatColor.UNDERLINE,
    ChatColor.ITALIC,
    ChatColor.RESET,
  )

  val suggest = (ChatColor.values().toList() - blacklist).toTypedArray()

  CommandAPICommand("namecolor")
    .withArguments(ChatColorArgument("color").replaceWithSafeSuggestions { suggest })
    .executesPlayer(PlayerCommandExecutor { player, args ->
      val color = when (args[0] as ChatColor) {
        ChatColor.BLACK -> NamedTextColor.BLACK
        ChatColor.DARK_BLUE -> NamedTextColor.DARK_BLUE
        ChatColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
        ChatColor.DARK_AQUA -> NamedTextColor.DARK_AQUA
        ChatColor.DARK_RED -> NamedTextColor.DARK_RED
        ChatColor.DARK_PURPLE -> NamedTextColor.DARK_PURPLE
        ChatColor.GOLD -> NamedTextColor.GOLD
        ChatColor.GRAY -> NamedTextColor.GRAY
        ChatColor.DARK_GRAY -> NamedTextColor.DARK_GRAY
        ChatColor.BLUE -> NamedTextColor.BLUE
        ChatColor.GREEN -> NamedTextColor.GREEN
        ChatColor.AQUA -> NamedTextColor.AQUA
        ChatColor.RED -> NamedTextColor.RED
        ChatColor.LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE
        ChatColor.YELLOW -> NamedTextColor.YELLOW
        ChatColor.WHITE -> NamedTextColor.WHITE
        else -> return@PlayerCommandExecutor
      }

      player.persistentDataContainer.set(NamespacedKey(plugin, "nameColor"), PersistentDataType.INTEGER, color.value())
      val message = Component.empty()
        .append(Component.text("Your name will now show up in chat as ").color(NamedTextColor.YELLOW))
        .append(Component.text("\"").color(NamedTextColor.DARK_GRAY))
        .append(player.displayName().color(color))
        .append(Component.text("\"").color(NamedTextColor.DARK_GRAY))

      player.sendMessage(message)
    })
    .register()
}

fun statsCommand() {
  CommandAPICommand("stats")
    .withArguments(StringArgument("player").replaceSuggestions { arrayOfOfflinePlayersNames() })
    .executes(CommandExecutor { sender, args ->
      val name = args[0] as String
      val player = Bukkit.getOfflinePlayerIfCached(name)

      if (player == null) {
        sender.sendMessage("&eWho's \"&f$name&e\"?".colored())
        return@CommandExecutor
      }

      val stats = PlayerStats(player)

      val stat: (String, Any) -> String = { name, value -> "&e$name&7: &f$value&r".colored() }
      val len = (52 - name.length) / 2

      sender.sendMessage("&7&m${" ".repeat(len)}[&r &e$name &7&m]${" ".repeat(len)}&r".colored())
      sender.sendMessage(stat("UUID", player.uniqueId))
      sender.sendMessage(stat("Online", stats.online))
      sender.sendMessage(stat("Deaths", stats.deaths))
      sender.sendMessage(stat("Mob Kills", stats.mobKills))
      sender.sendMessage(stat("Player Kills", stats.playerKills))
      sender.sendMessage(stat("Time Played", (stats.timePlayed / 20).seconds))
      sender.sendMessage(stat("Last Seen", stats.lastSeen))
      sender.sendMessage(stat("Last Login", stats.lastLogin))
      sender.sendMessage("&7&m${" ".repeat(len * 2 + name.length + 8)}&r".colored())
    })
    .register()
}