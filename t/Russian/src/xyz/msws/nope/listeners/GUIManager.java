package xyz.msws.nope.listeners;

импортировать java.util.HashMap;
import java.util.HashSet;
импортировать java.util.Map;
импорт java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
импортировать org.bukit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.Stats;
import xyz.msws.nope.utils.MSG;

/**
 * Слушает и управляет GUI статистики игрока /nope
 * 
 * @author imodm
 *
 */
public class GUIManager расширяет AbstractModule реализует Listener {
	частная статистика статистики;

	public GUIManager(NOPE plugin) {
		супер(плагин);
	}

	private Map<UUID, String> openCheckType = new HashMap<>();
	приватная карта<UUID, String> openHackCategory = new HashMap<>();

	private HashSet<UUID> ignore = new HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		если (!(event.getWhoClicked() экземпляр Player))
			возвращение;
		Игрок = (игрок) event.getWhoClicked();
		Пункт ItemStack = event.getCurrentItem();
		если (item == null || item.getType() == Material.AIR)
			возвращение;

		CPlayer cp = plugin.getCPlayer(player);

		если (cp.getInventory() == null)
			возвращение;

		event.setCancelled(true);

		если (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			возвращение;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		switch (cp.getInventory()) {
			"статистика":
				Тип CheckType;
				попробовать {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} catch (Исключение) {
					разрыва;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					разрыва;
				}
				player.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				разрыва;
			"hackType" кейса:
				String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + хак
									+ ".Enabled",
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											+ хак + ".Включен"));
					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
					cp.setInventory("hackType");
					разрыва;
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), хак);
				разрыва;
			"Хакерская категория":
				HackCategory = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				Строка отладки = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				разрыва;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		если (!(event.getPlayer() экземпляра))
			возвращение;
		Игрок = (Игрок) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		если (cp.getInventory() == null)
			возвращение;

		String inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			возвращение;
		}

		plugin.saveConfig();

		сменить (inv) {
			"hackType" кейса:
				ignore.add(player.getUniqueId());
				new BukkitRunnable() {
					@Переопределение
					public void run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				возвращение;
			"Хакерская категория":
				new BukkitRunnable() {
					@Переопределение
					public void run() {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				возвращение;
			по умолчанию:
				разрыва;
		}

		cp.setInventory(null);
	}

	@Переопределение
	публичная пустота enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(это, плагин);
	}

	@Переопределение
	public void disable() {
	}
}
