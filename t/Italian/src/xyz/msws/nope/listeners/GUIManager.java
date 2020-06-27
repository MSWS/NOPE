pacchetto xyz.msws.nope.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventario.InventoryClickEvent;
import org.bukkit.event.inventario.InventarioCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.Stats;
import xyz.msws.nope.utils.MSG;

/**
 * Ascolta e gestisce l'interfaccia delle statistiche /nope del giocatore
 * 
 * @author imodm
 *
 */
public class GUIManager extends AbstractModule implements Listener {
	statistiche statistiche private;

	public GUIManager(NOPE plugin) {
		super(plugin);
	}

	private Map<UUID, String> openCheckType = new HashMap<>();
	private Map<UUID, String> openHackCategory = new HashMap<>();

	privato HashSet<UUID> ignora = nuovo HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			restituisce;
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null <unk> <unk> item.getType() == Material.AIR)
			restituisce;

		CPlayer cp = plugin.getCPlayer(player);

		if (cp.getInventory() == null)
			restituisce;

		event.setCancelled(true);

		if (!item.hasItemMeta() <unk> <unk> !item.getItemMeta().hasDisplayName())
			restituisce;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		switch (cp.getInventory()) {
			caso "stats":
				Tipo di controllo;
				try {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} catch (Exception e) {
					rompere;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					giocatore.openInventory(stats.getInventory());
					cp.setInventory("stats");
					rompere;
				}
				giocatore.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				rompere;
			caso "hackType":
				String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Abilitato",
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."

					ignore.add(player.getUniqueId());
					giocatore.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
					cp.setInventory("hackType");
					rompere;
				}
				ignore.add(player.getUniqueId());
				giocatore.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				rompere;
			caso "hackCategory":
				String hackCategoria = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategoria + "." + debugName + ".Abilitato",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackCategoria + "." + debugName + ".Abilitato"));
				ignore.add(player.getUniqueId());
				giocatore.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				rompere;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			restituisce;
		Player player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (cp.getInventory() == null)
			restituisce;

		Stringa inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			restituisce;
		}

		plugin.saveConfig();

		switch (inv) {
			caso "hackType":
				ignore.add(player.getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						giocatore.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				restituisce;
			caso "hackCategory":
				new BukkitRunnable() {
					@Override
					public void run() {
						giocatore.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				restituisce;
			predefinito:
				rompere;
		}

		cp.setInventory(null);
	}

	@Override
	public void enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(this plugin);
	}

	@Override
	public void disable() {
	}
}
