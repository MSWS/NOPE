pakket xyz.msws.nope.listeners;

importeer java.util.HashMap;
import java.util.HashSet;
importeer java.util.Map;
importeer java.util.UID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
importeer org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
importeer org.bukkit.event.inventory.ClickType;
importeer org.bukkit.event.inventory.InventoryClickEvent;
importeer org.bukkit.event.inventory.InventoryCloseEvent;
importeer org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

importeer xyz.msws.nope.NEPE;
importeer xyz.msws.nope.modules.AbstractModule;
importeer xyz.msws.nope.modules.checks.CheckType;
importeer xyz.msws.nope.modules.data.CPlayer;
importeer xyz.msws.nope.modules.data.Stats;
importeer xyz.msws.nope.utils.MSG;

/**
 * Luistert en beheert de speler's /nope stats GUI
 * 
 * @auteur imodm
 *
 */
publieke klasse GUIManager breidt AbstractModule implementeert Listener {
	privaat statistieken

	publieke GUIManager(NOPE plugin) {
		super(plugin);
	}

	private kaart<UUID, String> openCheckType = new HashMap<>();
	privé kaart<UUID, String> openHackcategory = new HashMap<>();

	private HashSet<UUID> negeer = new HashSet<>();

	EventHandler
	openbare void onClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instantieof Player))
			retour;
		Speler speler = (Player) evenement. getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null ~: getType() == Material.AIR)
			retour;

		CPlayer cp = plugin.getCPlayer(speler);

		if (cp.getInventory() == null)
			retour;

		event.setCancelled(true);

		als (!item.hasItemMeta() configured !item.getItemMeta().hasDisplayName())
			retour;

		speler.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		switch (cp.getInventory()) {
			case "statistieken":
				CheckType type;
				probeer {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} vangst (Exception e) {
					pak;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Controle." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					pak;
				}
				player.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				pak;
			case "hackType":
				String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Controle." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Ingeschakeld",
							!plugin.getConfig()
									.getBoolean("Controlean("Controles." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											• • • Indicaal, IndiIndihack + ".Enabled"));
					negeer.add(player.getUniqueId());
					speler.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
					cp.setInventory("hackType");
					pak;
				}
				negeer.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				pak;
			case "hackCategorie":
				String hackcategorie = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				DebugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Controles." + hackType + "." + hackCategorie + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Controles." + hackType + "." + hackCategorie + "." + debugName + ".Enabled"));
				negeer.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				pak;
		}
	}

	EventHandler
	publieke void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instananceof Player))
			retour;
		Speler speler = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(speler);

		if (cp.getInventory() == null)
			retour;

		String inv = cp.getInventory();

		als (ignore.contains(player.getUniqueId())) {
			negeer.remove(player.getUniqueId());
			retour;
		}

		plugin.saveConfig();

		schakelaar (inv) {
			case "hackType":
				negeer.add(player.getUniqueId());
				nieuwe BukkitRunnable() {
					@Overschrijven
					publieke void run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				retour;
			case "hackCategorie":
				nieuwe BukkitRunnable() {
					@Overschrijven
					publieke void run() {
						speler.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				retour;
			standaard:
				pak;
		}

		cp.setInventory(null);
	}

	@Overschrijven
	publieke ongeldig enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(This plugin);
	}

	@Overschrijven
	publieke void disable() {
	}
}
