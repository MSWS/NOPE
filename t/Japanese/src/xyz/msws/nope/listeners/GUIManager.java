パッケージ xyz.msws.nope.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventoryClickEvent;
import org.bukkit.event.inventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.Stats;
import xyz.msws.nope.utils.MSG;

/**
 * プレイヤーの /nope 統計情報 GUI を聞いて管理します
 * 
 * @author imodm
 *
 */
public class GUIManager extends AbstractModule implements Listener {
	private Stats stats;

	public GUIManager(NOPE plugin) {
		super(plugin);
	}

	private Map<UUID, String> openCheckType = new HashMap<>();
	private Map<UUID, String> openHackCategory = new HashMap<>();

	private HashSet<UUID> ignore = new HashSet<>();

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player))
			返品;
		Player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			返品;

		CPlayer cp = plugin.getCPlayer(player);

		if (cp.getInventory() == null)
			返品;

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			返品;

		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

		switch (cp.getInventory()) {
			case "stats":
				チェックタイプ;
				try {
					type = CheckType.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).toUpperCase());
				} catch (Exception e) {
					休憩;
				}
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set("Checks" + MSG.camelCase(type + "") + ".Enabled",
							!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"));
					player.openInventory(stats.getInventory());
					cp.setInventory("stats");
					休憩;
				}
				player.openInventory(stats.getInventory(type));
				cp.setInventory("hackType");
				openCheckType.put(player.getUniqueId(),
						ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase());
				休憩;
			case "hackType":
				String hack = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if (event.getClick() == ClickType.RIGHT) {
					plugin.getConfig().set(
							"Checks" + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "." + hack hack
									+ ".Enabled",
							!plugin.getConfig()
									.getBoolean("Checks". + MSG.camelCase(openCheckType.get(player.getUniqueId())) + "."
											+ hack + ".Enabled");
					ignore.add(player.getUniqueId());
					player.openInventory(
							stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
					cp.setInventory("hackType");
					休憩;
				}
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hack));
				cp.setInventory("hackCategory");
				openHackCategory.put(player.getUniqueId(), hack);
				休憩;
			case "hackCategory":
				String hackCategory = openHackCategory.get(player.getUniqueId());
				String hackType = MSG.camelCase(openCheckType.get(player.getUniqueId()));
				String debugName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				plugin.getConfig().set("Checks" + hackType + "." + hackCategory + "." + debugName + ".Enabled",
						!plugin.getConfig()
								.getBoolean("Checks. + hackType + "." + hackCategory + "." + debugName + ".Enabled"));
				ignore.add(player.getUniqueId());
				player.openInventory(stats.getInventory(hackCategory ));
				cp.setInventory("hackCategory");
				休憩;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			返品;
		Player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (cp.getInventory() == null)
			返品;

		String inv = cp.getInventory();

		if (ignore.contains(player.getUniqueId())) {
			ignore.remove(player.getUniqueId());
			返品;
		}

		plugin.saveConfig();

		switch (inv) {
			case "hackType":
				ignore.add(player.getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						player.openInventory(stats.getInventory());
						cp.setInventory("stats");
					}
				}.runTaskLater(plugin, 1);
				返品;
			case "hackCategory":
				new BukkitRunnable() {
					@Override
					public void run() {
						player.openInventory(
								stats.getInventory(CheckType.valueOf(openCheckType.get(player.getUniqueId())));
						cp.setInventory("hackType");
					}
				}.runTaskLater(plugin, 1);
				返品;
			デフォルト:
				休憩;
		}

		cp.setInventory(null);
	}

	@Override
	public void enable() {
		this.stats = plugin.getModule(Stats.class);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disable() {
	}
}
