package me.ricky.essentials

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.milkbowl.vault.chat.Chat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class RickyEssentials : JavaPlugin() {
  override fun onEnable() {
    val chat = server.servicesManager.getRegistration(Chat::class.java)?.provider!!

    server.pluginManager.registerEvents(BetterChat(chat), this)
  }
}

class BetterChat(private val chat: Chat) : Listener {
  @EventHandler
  fun AsyncChatEvent.onChat() {
    player.displayName(
      Component.text("${chat.getPlayerPrefix(player)}${player.name}${chat.getPlayerSuffix(player)}".colored())
    )

    renderer { source, sourceDisplayName, message, viewer ->
      val textCfg = TextReplacementConfig
        .builder()
        .match("\\[[Ii][Tt][Ee][Mm]]")
        .once()
        .replacement(ComponentLike {
          val item = source.inventory.itemInMainHand

          val name = item.itemMeta
            ?.displayName()
            ?.color(NamedTextColor.AQUA)
            ?: Component.translatable(item)

          val amount = if (item.amount <= 1) Component.empty() else Component
            .text(" x")
            .append(Component.text(item.amount))
            .color(NamedTextColor.GRAY)

          Component.empty()
            .append(Component.text("[").color(NamedTextColor.DARK_GRAY))
            .append(name.append(amount).color(NamedTextColor.YELLOW))
            .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
            .hoverEvent(item)
        })

      val stats = PlayerStats(player)

      val stat: (String, Any) -> Component = { name, value ->
        Component.empty()
          .append(Component.text(name).color(NamedTextColor.YELLOW))
          .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
          .append(Component.text(value.toString()).color(NamedTextColor.WHITE))
      }

      val hoverText = listOf(
        stat("Deaths", stats.deaths),
        stat("Mob Kills", stats.mobKills),
        stat("Player Kills", stats.playerKills),
      )

      val hoverEvent = HoverEvent.showText(hoverText.asLines())

      Component.empty()
        .append(sourceDisplayName.hoverEvent(hoverEvent))
        .append(Component.text(": ").color(NamedTextColor.GRAY))
        .append(message.replaceText(textCfg.build()))
    }
  }
}