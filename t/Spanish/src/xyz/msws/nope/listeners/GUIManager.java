paquete xyz.msws.nope.listeners;

importar java.util.HashMap;
import java.util.HashSet;
importar java.util.Map;
importar el UID de java.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
importar org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
importar org.bukkit.event.inventory.ClickType;
importar org.bukkit.event.inventory.InventoryClickEvent;
importar org.bukkit.event.inventory.InventoryCloseEvent;
importar org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

importar xyz.msws.nope.NOPE;
importar xyz.msws.nope.modules.AbstractModule;
importar xyz.msws.nope.modules.checks.CheckType;
importar xyz.msws.nope.modules.data.CPlayer;
importar xyz.msws.nope.modules.data.Stats;
importar xyz.msws.nope.utils.MSG;

/**
 * Escucha y administra las estadísticas del jugador /nope GUI
 * 
 * imodm @author
 *
 */
public class GUIManager extends AbstractModule implementa Listener {
	estadísticas privadas;

	public GUIManager(NOPE plugin) {
		super(plugin);
	}

	privado Mapa<UUID, String> openCheckType = new HashMap<>();
	private Map<UUID, String> openHackCategory = new HashMap<>();

	HashSet privado<UUID> ignore = new HashSet<>();

	@EventManejador
	public void onClick(evento InventoryClickevent) {
		if (!(event.getWhoClicked() instancieof Player))
			regresar;
		Jugador = (Jugador) event.getWhoClicked();
		Ítem ItemStack = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			regresar;

		CPlayer cp = plugin.getCPlayer(jugador);

		if (cp.getInventory() == null)
			regresar;

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			regresar;

		player.playSound(player.getLocation(), Sonido_I_BUTTON_CLICK, 2, 1);

		switch (cp.getInventory()) {
			caso "estadísticas":
				Tipo de checkType;
				probar {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} catch (Excepción e) {
					romper;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					romper;
				}
				player.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				romper;
			caso "hackType":
				Hacha de cadena = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Enabled",
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											+ hack + ".Enabled"));
					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
					cp.setInventory("hackType");
					romper;
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				romper;
			caso "hackCategory":
				Categoría de cadena = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				DebugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory));
				cp.setInventory("hackCategory");
				romper;
		}
	}

	@EventManejador
	public void onClose(evento InventoryCloseEvento ) {
		if (!(event.getPlayer() instanceof Player))
			regresar;
		Jugador = (Jugador) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(jugador);

		if (cp.getInventory() == null)
			regresar;

		Cadena inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			regresar;
		}

		plugin.saveConfig();

		cambiar (inv) {
			caso "hackType":
				ignore.add(player.getUniqueId());
				nuevo BukkitRunnable() {
					@Sobreescribir
					public void run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				regresar;
			caso "hackCategory":
				nuevo BukkitRunnable() {
					@Sobreescribir
					public void run() {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				regresar;
			por defecto:
				romper;
		}

		cp.setInventory(null);
	}

	@Sobreescribir
	public void enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Sobreescribir
	public void disable() {
	}
}
