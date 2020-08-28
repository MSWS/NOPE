package xyz.msws.nope.checks.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.protocols.WrapperPlayServerSetSlot;

/**
 * Every so often, removes a player's armor piece and sees if they near
 * instantly put it back on
 * 
 * @author imodm
 *
 */
public class AutoArmor1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	private final int CHECK_EVERY = 400, WAIT_FOR = 1;

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		runCheck().runTaskTimer(plugin, 0, CHECK_EVERY);
	}

	private Map<UUID, Integer> slot = new HashMap<>();

	private BukkitRunnable runCheck() {
		return new BukkitRunnable() {
			public void run() {
				for (Player target : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(target);
					if (cp.timeSince(Stat.HORIZONTAL_BLOCKCHANGE) > 1000) // If the player is spam clicking within their
						// inventory
						continue;
					if (cp.timeSince(Stat.ON_GROUND) > 2000)
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

					WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot();
					packet.setSlot(open);
					packet.setSlotData(new ItemStack(Material.DIAMOND_HELMET));
					packet.sendPacket(target);
					slot.put(target.getUniqueId(), open);
					new BukkitRunnable() {
						@Override
						public void run() {
							WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot();
							packet.setSlot(open);
							packet.setSlotData(new ItemStack(Material.AIR));
							packet.sendPacket(target);
							slot.remove(target.getUniqueId());
						}
					}.runTaskLater(plugin, WAIT_FOR);
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

		if (!slot.containsKey(player.getUniqueId()))
			return;

		if (slot.get(player.getUniqueId()) != event.getRawSlot())
			return;

		event.setCancelled(true);
		event.setResult(Result.DENY);
		event.setCurrentItem(new ItemStack(Material.AIR));
		cp.flagHack(this, 50);
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
