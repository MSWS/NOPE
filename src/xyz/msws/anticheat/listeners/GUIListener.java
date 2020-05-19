package xyz.msws.anticheat.listeners;

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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.checks.Global.Stat;
import xyz.msws.anticheat.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

public class GUIListener implements Listener {
	private NOPE plugin;

	public GUIListener(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Map<UUID, String> openCheckType = new HashMap<>();
	private Map<UUID, String> openHackCategory = new HashMap<>();

	private HashSet<UUID> ignore = new HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			return;

		CPlayer cp = plugin.getCPlayer(player);

		if (!cp.hasTempData(Stat.OPEN_INVENTORY))
			return;

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		switch (cp.getTempString(Stat.OPEN_INVENTORY)) {
			case "stats":
				CheckType type;
				try {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} catch (Exception e) {
					break;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks." + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(plugin.getStats().getInventory());
					cp.setTempData(Stat.OPEN_INVENTORY, "stats");
					break;
				}
				player.openInventory(plugin.getStats().getInventory(type));
				cp.setTempData(Stat.OPEN_INVENTORY, "hackType");
//			cp.setTempData("openCheckType", );
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				break;
			case "hackType":
				String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack
									+ ".Enabled",
							!plugin.getConfig()
									.getBoolean("Checks." + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											+ hack + ".Enabled"));
//					cp.setTempData("ignoreInventory", 1);
					ignore.add(player.getUniqueId());
					player.openInventory(
							plugin.getStats().getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
					cp.setTempData(Stat.OPEN_INVENTORY, "hackType");
					break;
				}
//				cp.setTempData("ignoreInventory", 1);
				ignore.add(player.getUniqueId());
				player.openInventory(plugin.getStats().getInventory(hack));
				cp.setTempData(Stat.OPEN_INVENTORY, "hackCategory");
//				cp.setTempData("openHackCategory", hack);
				openHackCategory.put(player.getUniqueId(), hack);
				break;
			case "hackCategory":
				String hackCategory = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
//				cp.setTempData("ignoreInventory", 1);
				ignore.add(player.getUniqueId());
				player.openInventory(plugin.getStats().getInventory(hackCategory));
				cp.setTempData(Stat.OPEN_INVENTORY, "hackCategory");
				break;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			return;
		Player player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (!cp.hasTempData(Stat.OPEN_INVENTORY))
			return;

		String inv = cp.getTempString(Stat.OPEN_INVENTORY);

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			return;
		}

		plugin.saveConfig();

		switch (inv) {
			case "hackType":
//				cp.setTempData("ignoreInventory", 1);
				ignore.add(player.getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						player.openInventory(plugin.getStats().getInventory());
						cp.setTempData(Stat.OPEN_INVENTORY, "stats");
					}
				}.runTaskLater(plugin, 1);
				return;
			case "hackCategory":
				new BukkitRunnable() {
					@Override
					public void run() {
						player.openInventory(plugin.getStats()
								.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId()))));
						cp.setTempData(Stat.OPEN_INVENTORY, "hackType");
					}
				}.runTaskLater(plugin, 1);
				return;
			default:
				break;
		}

		cp.removeTempData(Stat.OPEN_INVENTORY);
	}
}
