Paket xyz.msws.nope.listeners;

importiere java.util.HashMap;
import java.util.HashSet;
importiere java.util.Map;
importiere java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
importieren org.bukkit.event.inventory.ClickType;
importieren org.bukkit.event.inventory.InventoryClickEvent;
importieren org.bukkit.event.inventory.InventoryCloseEvent;
importieren org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

importieren xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.checks.CheckType;
importieren xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.Stats;
importieren xyz.msws.nope.utils.MSG;

/**
 * Hört die /nope Statistiken des Spielers an und verwaltet sie
 * 
 * @author imodm
 *
 */
public class GUIManager erweitert AbstractModule implementiert Listener {
	private Statistik;

	öffentliches GUIManager(NOPE-Plugin) {
		super(plugin);
	}

	private Karte<UUID, String> openCheckType = new HashMap<>();
	private Karte<UUID, String> openHackCategory = new HashMap<>();

	privates HashSet<UUID> ignorieren = neues HashSet<>();

	@Eventhandler
	public void onClick(InventoryClickEvent Event) {
		if (!(event.getWhoClicked() instanceof Player))
			zurückkehren;
		Player Player = (Player) event.getWhoClicked();
		Artikel Stack Item = event.getCurrentItem();
		if (Element == null || item.getType() == Material.AIR)
			zurückkehren;

		CPlayer cp = plugin.getCPlayer(player);

		if (cp.getInventory() == null)
			zurückkehren;

		event.setCancelled(wahr);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			zurückkehren;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		switch (cp.getInventory()) {
			case "stats":
				CheckType Typ;
				versuche {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} Fang (Ausnahme) {
					bruch;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(Typ + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					bruch;
				}
				player.openInventory(stats.getInventory(typ));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				bruch;
			Fall "hackType":
				Stringhack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Aktiviert",
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											+ Hack + „.Enabled“);
					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
					cp.setInventory("hackType");
					bruch;
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				bruch;
			Fall "HackKategorie":
				String hackcategory = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId());
				String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackcategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				bruch;
		}
	}

	@Eventhandler
	public void onClose(InventoryCloseEvent Event) {
		if (!(event.getPlayer() instance))
			zurückkehren;
		Player Player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (cp.getInventory() == null)
			zurückkehren;

		String inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			zurückkehren;
		}

		plugin.saveConfig();

		wechseln (inv) {
			Fall "hackType":
				ignore.add(player.getUniqueId());
				neue BukkitRunnable() {
					@Überschreiben
					public void run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(Plugin, 1);
				zurückkehren;
			Fall "HackKategorie":
				neue BukkitRunnable() {
					@Überschreiben
					public void run() {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
						cp.setInventory("hackType");
					}
				}.runTaskLater(Plugin, 1);
				zurückkehren;
			default:
				bruch;
		}

		cp.setInventory(null);
	}

	@Überschreiben
	public void enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Überschreiben
	public void disable() {
	}
}
