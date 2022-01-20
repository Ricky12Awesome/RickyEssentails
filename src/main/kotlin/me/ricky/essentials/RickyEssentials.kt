package me.ricky.essentials

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class RickyEssentials : JavaPlugin() {
  override fun onEnable() {
    server.pluginManager.registerEvents(BetterChat(), this)
  }
}

class BetterChat : Listener {
  @EventHandler
  fun AsyncChatEvent.onChat() {


    renderer { source, sourceDisplayName, message, viewer ->
      val textCfg = TextReplacementConfig
        .builder()
        .match("\\[[Ii][Tt][Ee][Mm]]")
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

      Component.empty()
        .append(sourceDisplayName)
        .append(Component.text(": ").color(NamedTextColor.GRAY))
        .append(message.replaceText(textCfg.build()))
    }
  }
}