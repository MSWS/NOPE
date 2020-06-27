package xyz.msws.nope.listeners;

importuj java.util.HashMap;
import java.util.HashSet;
importuj java.util.Map;
importowanie java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
zaimportuj org.bukit.entity.Player,
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
importuj org.bukit.event.inventory.ClickType;
importuj org.bukkit.event.inventory.InventoryClickEvent;
importuj org.bukkit.event.inventory.InventoryCloseEvent;
importuj org.bukit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
importuj xyz.msws.nope.modules.AbstractModule;
importuj xyz.msws.nope.modules.checks.CheckType;
importuj xyz.msws.nope.modules.data.CPlayer,
importuj xyz.msws.nope.modules.data.Stats;
import xyz.msws.nope.utils.MSG;

/**
 * Słuchuje interfejsu graficznego / nope gracza i zarządza nim
 * 
 * @author imodm
 *
 */
publiczna klasa GUIManager rozszerza AbstractModule implements Listener {
	statystyki prywatne;

	publiczny GUIManager(NOPE plugin) {
		super(wtyczka);
	}

	prywatna mapa<UUID, String> openCheckType = nowa HashMap<>();
	prywatna mapa<UUID, String> openHackCategory = new HashMap<>();

	prywatny HashSet<UUID> ignore = new HashSet<>();

	@EventHandler
	publiczna unieważnienie onClick(InventoryClickEvent event) {
		jeśli (!(event.getWhoClicked() instanceof Player))
			powrot;
		Gracz = (Player) event.getWhoClicked();
		Przedmiot Stack = event.getCurrentItem();
		if (element == null || item.getType() == Material.AIR)
			powrot;

		CPlayer cp = plugin.getCPlayer(gracz);

		if (cp.getInventory() == null)
			powrot;

		event.setAnulowane (true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			powrot;

		gracz.playSound(gracz.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		przełącznik (cp.getInventory()) {
			"statystyki":
				Typ kontroli;
				spróbuj {
					typ = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} połów (założenie e) {
					przerwa;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					gracz.openInventory(stats.getInventory());
					cp.setInventory("stats");
					przerwa;
				}
				gracz.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toperCase());
				przerwa;
			przypadek „hackType”:
				Hacking hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Sprawdzania" + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Enabled",
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(gracz.getUniqueId())) + "."

					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
					cp.setInventory("hackType");
					przerwa;
				}
				ignore.add(player.getUniqueId());
				gracz.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				przerwa;
			przypadek „hackCategory”:
				hackCategory ciągu = openHackCategory.get(player.getUniqueId());
				hackType string = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				DebugName ciągu = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				gracz.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				przerwa;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		jeśli (!(event.getPlayer() instanceof Player))
			powrot;
		Gracz = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(gracz);

		if (cp.getInventory() == null)
			powrot;

		String inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			powrot;
		}

		plugin.saveConfig();

		przełącznik (inv) {
			przypadek „hackType”:
				ignore.add(player.getUniqueId());
				nowy BukkitRunnable() {
					@Nadpisz
					publiczna unieważnienie () {
						gracz.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				powrot;
			przypadek „hackCategory”:
				nowy BukkitRunnable() {
					@Nadpisz
					publiczna unieważnienie () {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				powrot;
			domyślnie:
				przerwa;
		}

		cp.setInventory(null);
	}

	@Nadpisz
	włączono ubytek () {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(this plugin);
	}

	@Nadpisz
	ubytek wyłączony() {
	}
}
