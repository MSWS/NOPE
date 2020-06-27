pakke xyz.msws.nope.listenere;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
importer org.bukkit.entity.Spiller;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
importer org.bukkit.event.inventory.ClickType;
importer org.bukkit.event.inventory.InventoryClickEvent;
importer org.bukkit.event.inventory.InventoryCloseEvent;
importer org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

importer xyz.msws.nope.NOPE;
Importer xyz.msws.nope.modules.AbstractModule;
Importer xyz.msws.nope.modules.checks.CheckType;
Importer xyz.msws.nope.modules.data.CPlayer;
Importer xyz.msws.nope.modules.data.Stats;
Importer xyz.msws.nope.utils.MSG;

/**
 * Lytter til og administrerer spillerens /nettverk statistikk GUI
 * 
 * @forfatter imodm
 *
 */
offentlig klasse GUIManager utvider AbstractModule implementerer Listener {
	private statistiske tilstander;

	offentlig GUIManager(NOPE plugin) {
		super(plugin);
	}

	privat kart<UUID, String> openCheckType = nye HashMap<>();
	privat kart<UUID, String> openHackKategori = nye HashMap<>();

	privat hashSet<UUID> ignorer = ny HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		hvis (!(hendelse.getWhoClicked() instans av Player))
			retur;
		Spillerspiller = (Spiller) hendelse.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		hvis (element == null ε″unnelbitem.getType() == Material.AIR)
			retur;

		CPlayer cp = plugin.getCPlayer(spiller);

		hvis (cp.getInventory() == null)
			retur;

		hendelse.setkansellert(true);

		hvis (!item.hasItemMeta() A1!item.getItemMeta().hasDisplayName())
			retur;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		bytt (cp.getInventory()) {**
			tilfelle "statistikk":
				Type kontroll;
				prøv {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} fangst (unntak) {
					brudd;
				}
				hvis (event.getClick() == ClickType.RIGHT) {**
					plugin.getConfig().set("Kontroller." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Sjekker" + MSG.camelCase(type + "") + ".Aktivert"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					brudd;
				}
				player.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				brudd;
			tilfelle "hackType":
				Streng hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				hvis (event.getClick() == ClickType.RIGHT) {**
					plugin.getConfig().set(
							"Checks" + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".aktivert",
							!plugin.getConfig()
									.getBoolean("Kontroller." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											########################εephalephalephalephalephalephalephalephal(A) (A) + hack + ".Enabled"));
					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
					cp.setInventory("hackType");
					brudd;
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				brudd;
			tilfelle "hackCategory":
				String hackKategori = openHackCategory.get(player.getUniqueId());
				Streng hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				Streng debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackKategori + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Kontroller." + hackType + "." + hackKategori + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				brudd;
		}
	}

	@EventHandler
	offentlig tomrom (InventoryClEvent event) {
		hvis (!(event.getPlayer() instancof Player))
			retur;
		Spillerspiller = (Spiller) arrangement.getPlayer();
		CPlayer cp = plugin.getCPlayer(spiller);

		hvis (cp.getInventory() == null)
			retur;

		String inv = cp.getInventory();

		hvis (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			retur;
		}

		plugin.saveConfig();

		bryter (inv) {
			tilfelle "hackType":
				ignore.add(player.getUniqueId());
				ny BukkitRunnable() {
					@Overstyring
					offentlighet run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				retur;
			tilfelle "hackCategory":
				ny BukkitRunnable() {
					@Overstyring
					offentlighet run() {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				retur;
			standard:
				brudd;
		}

		cp.setInventory(null);
	}

	@Overstyring
	offentlig aktivering() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(denne, plugin);
	}

	@Overstyring
	public void disable() {
	}
}
