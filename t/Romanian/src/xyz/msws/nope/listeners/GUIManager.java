pachetul xyz.msws.nope.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
importă org.bukkit.entity.Player;
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
 * Ascultă și gestionează statisticile jucătorului /nope GUI
 * 
 * @autor imodm
 *
 */
public class GUIManager extinde AbstractModule implementează Listener {
	statistici statistice private;

	public GUIManager(NOPE plugin) {
		super(plugin);
	}

	Harta privată<UUID, String> openCheckType = noua HashMap<>();
	Harta privată<UUID, String> openHackcategory = hashMap nou<>();

	HashSet privat<UUID> ignore = new HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		dacă (!(event.getWhoClicked() instanță Player))
			întoarcere;
		Player = (Player) event.getWhoClicked();
		Element ItemStack = event.getCurrent Item();
		if (item == null <unk> item.getType() == Material.AIR)
			întoarcere;

		CPlayer cp = plugin.getCPlayer(jucător);

		dacă (cp.getInventory() == null)
			întoarcere;

		event.setCancelled(true);

		dacă (!item.hasItemMeta() <unk> !item.getItemMeta().hasDisplayName())
			întoarcere;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		comutator (cp.getInventory()) {
			(state) cazul "statistici":
				Tip de verificare;
				încercaţi {
					tip = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} captura (excepţie) {
					pauză;
				}
				dacă (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(tip + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks" + MSG.camelCase(type + "") + ".Enabled"));
					jucator.openInventory(stats.getInventory());
					cp.setInventory("stats");
					pauză;
				}
				jucator.openInventory(stats.getInventory(tip));
				cp.setInventory("hackType");
				openCheckType.put(jucator.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase());
				pauză;
			cazul "hackType":
				String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				dacă (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks." + MSG.camelCase(openCheckType.get(jucator.getUniqueId())) + "." + hack
									+ ".Activat",
							!plugin.getConfig()
									.getBoolean("Checks" + MSG.camelCase[openCheckType.get(jucator.getUniqueId())) + "."
											<unk> <unk> <unk> <unk> <unk> + hack + ".Enabled"));
					ignore.add(player.getUniqueId());
					player.openInventoriy(
							stats.getInventory(CheckType.valueOf(openCheckType.get(jucator.getUniqueId())));
					cp.setInventory("hackType");
					pauză;
				}
				ignore.add(player.getUniqueId());
				jucator.openInventory(stats.getInventory(hack));
				cp.setInventar ("hackCategoriy");
				openHackCategory.put(jucator.getUniqueId(), hack);
				pauză;
			cazul "hackCategorie":
				String hackCategory = openHackCategory.get(jucator.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId());
				String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackcategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				jucator.openInventory(stats.getInventory(hackCategory));
				cp.setInventar ("hackCategoriy");
				pauză;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanța Player))
			întoarcere;
		Player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(jucător);

		dacă (cp.getInventory() == null)
			întoarcere;

		şir inv = cp.getInventory();

		dacă (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			întoarcere;
		}

		plugin.saveConfig();

		comutați (inv) {
			cazul "hackType":
				ignore.add(player.getUniqueId());
				BukkitRunnable() nou {
					@Suprascriere
					vid public run() {
						jucator.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				întoarcere;
			cazul "hackCategorie":
				BukkitRunnable() nou {
					@Suprascriere
					vid public run() {
						player.openInventoriy(
								stats.getInventory(CheckType.valueOf(openCheckType.get(jucator.getUniqueId())));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				întoarcere;
			implicit:
				pauză;
		}

		cp.setInventory(null);
	}

	@Suprascriere
	evitați public enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(aceasta, plugin);
	}

	@Suprascriere
	anulați dezactivarea publică() {
	}
}
