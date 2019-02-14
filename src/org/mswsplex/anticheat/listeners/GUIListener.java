package org.mswsplex.anticheat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;
import org.mswsplex.anticheat.utils.Sounds;

public class GUIListener implements Listener {
	private AntiCheat plugin;

	public GUIListener(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			return;

		CPlayer cp = plugin.getCPlayer(player);

		if (!cp.hasTempData("openInventory"))
			return;

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return;

		player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 2, 1);

		switch (cp.getTempString("openInventory")) {
		case "stats":
			CheckType type;
			try {
				type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
			} catch (Exception e) {
				break;
			}
			if (event.getClick() == ClickType.RIGHT) {
				plugin.config.set("Checks." + MSG.camelCase(type + "") + ".Enabled",
						!plugin.config.getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
				player.openInventory(plugin.getStats().getInventory());
				cp.setTempData("openInventory", "stats");
				break;
			}
			player.openInventory(plugin.getStats().getInventory(type));
			cp.setTempData("openInventory", "hackType");
			cp.setTempData("openCheckType", ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
			break;
		case "hackType":
			String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
			if (event.getClick() == ClickType.RIGHT) {
				plugin.config.set(
						"Checks." + MSG.camelCase(cp.getTempString("openCheckType")) + "." + hack + ".Enabled",
						!plugin.config.getBoolean("Checks." + MSG.camelCase(cp.getTempString("openCheckType")) + "."
								+ hack + ".Enabled"));
				cp.setTempData("ignoreInventory", 1);
				player.openInventory(
						plugin.getStats().getInventory(CheckType.valueOf(cp.getTempString("openCheckType"))));
				cp.setTempData("openInventory", "hackType");
				break;
			}
			cp.setTempData("ignoreInventory", 1);
			player.openInventory(plugin.getStats().getInventory(hack));
			cp.setTempData("openInventory", "hackCategory");
			cp.setTempData("openHackCategory", hack);
			break;
		case "hackCategory":
			String hackCategory = cp.getTempString("openHackCategory");
			String hackType = MSG.camelCase(cp.getTempString("openCheckType"));
			String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
			plugin.config.set("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled", !plugin.config
					.getBoolean("Checks." + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
			cp.setTempData("ignoreInventory", 1);
			player.openInventory(plugin.getStats().getInventory(hackCategory));
			cp.setTempData("openInventory", "hackCategory");
			break;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			return;
		Player player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		String inv = cp.getTempString("openInventory");

		if (cp.getTempInteger("ignoreInventory") > 0) {
			cp.setTempData("ignoreInventory",
					cp.getTempInteger("ignoreInventory") == 0 ? 0 : cp.getTempInteger("ignoreInventory") - 1);
			return;
		}

		plugin.saveConfig();

		switch (inv) {
		case "hackType":
			cp.setTempData("ignoreInventory", 1);
			new BukkitRunnable() {
				@Override
				public void run() {
					player.openInventory(plugin.getStats().getInventory());
					cp.setTempData("openInventory", "stats");
				}
			}.runTaskLater(plugin, 1);
			return;
		case "hackCategory":
			new BukkitRunnable() {
				@Override
				public void run() {
					player.openInventory(
							plugin.getStats().getInventory(CheckType.valueOf(cp.getTempString("openCheckType"))));
					cp.setTempData("openInventory", "hackType");
				}
			}.runTaskLater(plugin, 1);
			return;
		default:
			break;
		}

		cp.removeTempData("openInventory");
	}
}
