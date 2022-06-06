package me.ricky.essentials

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.time.Duration.Companion.seconds

@Suppress("unused")
class RickyEssentials : JavaPlugin() {
  override fun onEnable() {
    val betterChat = BetterChat(this) {
      if (server.pluginManager.getPlugin("Vault")?.isEnabled == true) {
        val chat = server.servicesManager.getRegistration(net.milkbowl.vault.chat.Chat::class.java)?.provider
          ?: return@BetterChat

        it.displayName(Component.text("${chat.getPlayerPrefix(it)}${it.name}${chat.getPlayerSuffix(it)}".colored()))
      }
    }

    server.pluginManager.registerEvents(betterChat, this)
    server.pluginManager.registerEvents(EditSign(), this)

    statsCommand()
    nameColorCommand(this)
    toggleVanillaChatCommand(this)
  }
}

class EditSign : Listener {
  @EventHandler
  fun PlayerInteractEvent.onSignClick() {
    val sign = clickedBlock?.state as? Sign ?: return

    if (action.isRightClick && player.isSneaking && player.inventory.itemInMainHand.type == Material.AIR) {
      player.openSign(sign)
    }
  }
}

class BetterChat(
  private val plugin: Plugin,
  private val setDisplayName: (Player) -> Unit = {}
) : Listener {
  fun itemReplacementConfig(item: ItemStack): TextReplacementConfig {
    val name = item.itemMeta
      ?.displayName()
      ?.color(NamedTextColor.AQUA)
      ?: Component.translatable(item)

    val amount = if (item.amount <= 1) Component.empty() else Component
      .text(" x")
      .append(Component.text(item.amount))
      .color(NamedTextColor.GRAY)

    val replacement = Component.empty()
      .append(Component.text("[").color(NamedTextColor.DARK_GRAY))
      .append(name.append(amount).color(NamedTextColor.YELLOW))
      .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
      .hoverEvent(item)

    return TextReplacementConfig
      .builder()
      .match("\\[[Ii][Tt][Ee][Mm]]")
      .once()
      .replacement(replacement)
      .build()
  }

  @EventHandler
  fun AsyncChatEvent.onChat() {
    setDisplayName(player)

    renderer { source, sourceDisplayName, message, audience ->
      val viewer = audience as? Player
      val useVanillaChatValue = viewer?.persistentDataContainer?.getOrDefault(
        NamespacedKey(plugin, "useVanillaChat"),
        PersistentDataType.INTEGER, 0
      ) ?: 0

      val useVanillaChat = useVanillaChatValue > 0

      if (useVanillaChat) {
        val hoverText = listOf(
          Component.text(player.name),
          Component.text("Type: ").append(Component.translatable(player.type.translationKey())),
          Component.text(player.uniqueId.toString()),
        )

        val hoverEvent = HoverEvent.showText(hoverText.asLines())

        Component.empty()
          .append(Component.text("<"))
          .append(sourceDisplayName
            .hoverEvent(hoverEvent)
            .clickEvent(ClickEvent.suggestCommand("/tell ${source.name}"))
          )
          .append(Component.text("> "))
          .append(message)
      } else {
        val item = source.inventory.itemInMainHand
        val itemReplacementConfig = itemReplacementConfig(item)
        val stats = PlayerStats(source)

        val stat: (String, Any) -> Component = { name, value ->
          Component.empty()
            .append(Component.text(name).color(NamedTextColor.YELLOW))
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(value.toString()).color(NamedTextColor.WHITE))
        }

        val hoverText = listOf(
          stat("Time Played", (stats.timePlayed / 20).seconds),
          stat("Deaths", stats.deaths),
          stat("Mob Kills", stats.mobKills),
          stat("Player Kills", stats.playerKills),
        )

        val hoverEvent = HoverEvent.showText(hoverText.asLines())
        val nameColorValue = source.persistentDataContainer.getOrDefault(
          NamespacedKey(plugin, "nameColor"),
          PersistentDataType.INTEGER,
          NamedTextColor.WHITE.value()
        )

        val nameColor = NamedTextColor.nearestTo { nameColorValue }

        Component.empty()
          .append(
            sourceDisplayName
              .color(nameColor)
              .hoverEvent(hoverEvent)
              .clickEvent(ClickEvent.suggestCommand("/msg ${source.name}"))
          )
          .append(Component.text(": ").color(NamedTextColor.GRAY))
          .append(message.replaceText(itemReplacementConfig))
      }
    }
  }
}