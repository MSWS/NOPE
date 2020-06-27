package xyz.msws.nope.listeners;

importera java.util.HashMap;
import java.util.HashSet;
importera java.util.Map;
importera java.util.UUID

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
importera org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickHändelse;
import org.bukkit.event.inventory.InventoryCloseHändelse;
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable;

importera xyz.msws.nope.NOPE;
importera xyz.msws.nope.modules.AbstractModule;
importera xyz.msws.nope.modules.checks.CheckType;
importera xyz.msws.nope.modules.data.CPlayer;
importera xyz.msws.nope.modules.data.Stats;
import xyz.msws.nope.utils.MSG;

/**
 * Lyssnar på och hanterar spelarens /nope statistik GUI
 * 
 * @author imodm
 *
 */
GUIManager utökar AbstractModule implementerar Listener {
	privat statistik statistik

	offentliga GUIManager (NOPE plugin) {
		super(plugin)
	}

	privat karta<UUID, String> openCheckType = new HashMap<>();
	privat karta<UUID, String> openHackCategory = ny HashMap<>();

	privat HashSet<UUID> ignore = new HashSet<>();

	@Eventhanterare
	public void onClick(InventoryClickEvent händelse) {
		om (!(event.getWhoClicked() instanceof Player))
			tillbaka
		Spelare = (Spelare) event.getWhoClicked();
		ItemStack objekt = event.getCurrentItem();
		om (artikel == null <unk> <unk> item.getType() == Material.AIR)
			tillbaka

		CPlayer cp = plugin.getCPlayer (spelare);

		om (cp.getInventory() == null)
			tillbaka

		event.setCancelled(true);

		om (!item.hasItemMeta() <unk> !item.getItemMeta().hasDisplayName())
			tillbaka

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		växla (cp.getInventory()) {
			fall "statistik":
				Typ av CheckType
				prova {
					typ = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} fångst (Undantag) {
					paus
				}
				om (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Kontroller." + MSG.camelCase(typ + "") + ".Aktiverad",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(typ + "") + ".Aktiverad"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					paus
				}
				player.openInventory(stats.getInventory(typ));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				paus
			fall "hackType":
				Sträng hacka = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				om (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Kontroller." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Aktiverad",
							!plugin.getConfig()
									.getBoolean("Kontroller." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											<unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> <unk> + hack + ".Aktiverad"));
					ignore.add(player.getUniqueId());
					spelare.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
					cp.setInventory("hackType");
					paus
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackKategori");
				openHackCategory.put(player.getUniqueId(), hack);
				paus
			fall "hackKategori":
				Sträng hackKategori = openHackCategory.get(player.getUniqueId());
				Sträng hackTyp = MSG.camelCase(openCheckType.get(player.getUniqueId());
				Sträng debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + "." + debugName + ".Aktiverad",
						!plugin.getConfig()
								.getBoolean("Kontroller." + hackType + "." + hackKategori + "." + debugName + ".Aktiverad"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackKategori));
				cp.setInventory("hackKategori");
				paus
		}
	}

	@Eventhanterare
	public void onClose(InventoryCloseEvent event) {
		om (!(event.getPlayer() instanceof Player))
			tillbaka
		Spelare = (Spelare) event.getPlayer();
		CPlayer cp = plugin.getCPlayer (spelare);

		om (cp.getInventory() == null)
			tillbaka

		Sträng inv = cp.getInventory();

		om (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			tillbaka
		}

		plugin.saveConfig();

		växla (inv) {
			fall "hackType":
				ignore.add(player.getUniqueId());
				ny BukkitRunnable() {
					@Åsidosätt
					offentlig annullering run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				tillbaka
			fall "hackKategori":
				ny BukkitRunnable() {
					@Åsidosätt
					offentlig annullering run() {
						spelare.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				tillbaka
			standard:
				paus
		}

		cp.setInventory(null);
	}

	@Åsidosätt
	public void enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(denna, plugin);
	}

	@Åsidosätt
	public void disable() {
	}
}
