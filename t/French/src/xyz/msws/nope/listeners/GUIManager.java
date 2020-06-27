paquet xyz.msws.nope.listeners;

Importer java.util.HashMap;
import java.util.HashSet;
Importer java.util.Map ;
Importer java.util.UUID ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
Importer org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
Importer org.bukkit.event.inventory.ClickType;
Importer org.bukkit.event.inventory.InventoryClickEvent;
importer org.bukkit.event.inventory.InventoryCloseEvent;
Importer org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

Importer xyz.msws.nope.NOPE ;
Importer xyz.msws.nope.modules.AbstractModule ;
Importer xyz.msws.nope.modules.checks.CheckType;
Importer xyz.msws.nope.modules.data.CPlayer;
Importer xyz.msws.nope.modules.data.Stats;
Importer xyz.msws.nope.utils.MSG;

/**
 * Écoute et gère l'interface des statistiques /nope du joueur
 * 
 * @author imodm
 *
 */
GUIManager de classe publique étend AbstractModule implémente Listener {
	statistiques privées;

	Gestionnaire graphique public (plugin NOPE) {
		super(plugin);
	}

	carte privée<UUID, String> openCheckType = new HashMap<>();
	carte privée<UUID, String> openHackCategory = new HashMap<>();

	HashSet privé<UUID> ignore = new HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent événement) {
		if (!(event.getWhoClicked() instanceof Player))
			retourner;
		Joueur = (joueur) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			retourner;

		CPlayer cp = plugin.getCPlayer(joueur);

		if (cp.getInventory() == null)
			retourner;

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			retourner;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		interrupteur (cp.getInventory()) {
			casse "stats":
				Type de CheckType;
				essayer {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} catch (Exception e) {
					casse ;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Vérifications." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					casse ;
				}
				player.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				casse ;
			case "hackType":
				Hack de chaîne = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Vérifications." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Activé",
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											+ hack + ".Enabled"));
					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
					cp.setInventory("hackType");
					casse ;
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				casse ;
			cas "hackCategory":
				String hackCategory = openHackCategory.get(player.getUniqueId());
				HackType de chaîne = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Vérifications." + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				casse ;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			retourner;
		Joueur = (joueur) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(joueur);

		if (cp.getInventory() == null)
			retourner;

		Chaîne inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			retourner;
		}

		plugin.saveConfig();

		switch (inv) {
			case "hackType":
				ignore.add(player.getUniqueId());
				new BukkitRunnable() {
					@Remplacer
					public void run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				retourner;
			cas "hackCategory":
				new BukkitRunnable() {
					@Remplacer
					public void run() {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				retourner;
			par défaut:
				casse ;
		}

		cp.setInventory(null);
	}

	@Remplacer
	public void enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(ceci, plugin);
	}

	@Remplacer
	public void disable() {
	}
}
