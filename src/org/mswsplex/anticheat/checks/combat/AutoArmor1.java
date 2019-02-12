package org.mswsplex.anticheat.checks.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

/**
 * Every so often, removes a player's armor piece and sees if they near
 * instantly put it back on
 * 
 * @author imodm
 *
 */
public class AutoArmor1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	private final int CHECK_EVERY = 600, WAIT_FOR = 1;

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		runCheck().runTaskTimer(plugin, 0, CHECK_EVERY);
	}

	private BukkitRunnable runCheck() {
		return new BukkitRunnable() {
			public void run() {
				for (Player target : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(target);
					if (cp.timeSince("lastDamageTaken") < 1000)
						continue;
					if (cp.timeSince("lastInventoryClick") < 500) // If the player is spam clicking within their
																	// inventory
						continue;
					Inventory inv = target.getInventory();
					List<Integer> opens = new ArrayList<>();
					for (int i = 9; i <= 35; i++) {
						if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
							opens.add(i);
						}
					}
					if (opens.size() <= 3)
						continue;
					int open = opens.get(ThreadLocalRandom.current().nextInt(opens.size()));
					EntityEquipment equipment = target.getEquipment();
					if (equipment.getArmorContents() == null)
						continue;
					for (int i = 0; i < equipment.getArmorContents().length; i++) {
						ItemStack armor = equipment.getArmorContents()[i];
						if (armor == null || armor.getType() == Material.AIR)
							continue;
						ItemStack[] newArmor = equipment.getArmorContents(), oldArmor = newArmor.clone();
						newArmor[i] = new ItemStack(Material.AIR);
						equipment.setArmorContents(newArmor);
						inv.setItem(open, armor);
						cp.setTempData("autoArmorSlot", open);
						new BukkitRunnable() {
							@Override
							public void run() {
								inv.setItem(cp.getTempInteger("autoArmorSlot"), new ItemStack(Material.AIR));
								target.getEquipment().setArmorContents(oldArmor);
								cp.removeTempData("autoArmorSlot");
							}
						}.runTaskLater(plugin, WAIT_FOR);
						break;
					}
				}
			}
		};
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData("lastInventoryClick", (double) System.currentTimeMillis());

		if (cp.getTempInteger("autoArmorSlot") != event.getRawSlot())
			return;

		event.setCancelled(true);
		runCheck().runTaskLater(plugin, ThreadLocalRandom.current().nextInt(10 * 20, 30 * 20));
		cp.flagHack(this, 100);
	}

	@Override
	public String getCategory() {
		return "AutoArmor";
	}

	@Override
	public String getDebugName() {
		return "AutoArmor#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
