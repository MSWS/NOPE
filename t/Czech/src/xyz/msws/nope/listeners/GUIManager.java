balíček xyz.msws.nope.listeners;

importovat java.util.HashMap;
import java.util.HashSet;
importovat java.util.Map;
importovat java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
importovat org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

importovat xyz.msws.nope.NOPE;
importovat xyz.msws.nope.modules.AbstractModule;
importovat xyz.msws.nope.modules.checks.CheckType;
importovat xyz.msws.nope.modules.data.CPlayer;
importovat xyz.msws.nope.modules.data.Stats;
importovat xyz.msws.nope.utils.MSG;

/**
 * Poslouchá a spravuje hráčovy statistiky / nope GUI
 * 
 * @autor imodm
 *
 */
veřejná třída GUIManager rozšiřuje AbstractModul implementuje Listener {
	soukromé statistiky;

	veřejný GUIManager(NOPE plugin) {
		super(plugin);
	}

	soukromá mapa<UUID, String> openCheckType = nový HashMap<>();
	soukromá mapa<UUID, String> openHackCategory = nový HashMap<>();

	soukromý HashSet<UUID> ignorovat = nový HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent událost) {
		if (!(event.getWhoClicked() instanceof Player))
			návrat;
		Hráč = (Player) event.getWhoClicked();
		ItemStack položka = event.getCurrentItem();
		if (položka == null || item.getType() == Material.AIR)
			návrat;

		CPlayer cp = plugin.getCPlayer(hráč);

		Pokud (cp.getInventory() == null)
			návrat;

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			návrat;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		Přepínač (cp.getInventory()) {
			písmena "statistiky":
				typ kontroly;
				zkus {
					typ = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} úlovek (xception e) {
					přerušení;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					přerušení;
				}
				player.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				přerušení;
			případ "hackType":
				String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Kontrolní" + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Povoleno",
							!plugin.getConfig()
									.getBoolean("Kontrola" + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											+ hack + ".Povoleno"));
					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
					cp.setInventory("hackType");
					přerušení;
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				přerušení;
			případ "hackCategory":
				String hackCategory = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Epovoleno",
						!plugin.getConfig()
								.getBoolean("Kontrola" + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				přerušení;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			návrat;
		Přehrávač = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(hráč);

		Pokud (cp.getInventory() == null)
			návrat;

		String inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			návrat;
		}

		plugin.saveConfig();

		přepínač (inv) {
			případ "hackType":
				ignore.add(player.getUniqueId());
				nový BukkitRunnable() {
					@override
					public void run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				návrat;
			případ "hackCategory":
				nový BukkitRunnable() {
					@override
					public void run() {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				návrat;
			výchozí:
				přerušení;
		}

		cp.setInventory(null);
	}

	@override
	public void enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@override
	public void disable() {
	}
}
