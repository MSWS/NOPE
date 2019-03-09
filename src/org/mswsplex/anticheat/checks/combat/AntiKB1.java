package org.mswsplex.anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Basically {@link NoFall} but when the player hits an entity
 * 
 * @author imodm
 *
 */
public class AntiKB1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();

		CPlayer cp = plugin.getCPlayer(player);

		if (player.isInsideVehicle())
			return;

		if (cp.isBlockAbove())
			return;

		if (cp.timeSince("lastLiquid") < 500)
			return;

		if (cp.isBlockNearby(Material.WEB))
			return;

		Location origin = player.getLocation();

		new BukkitRunnable() {
			@Override
			public void run() {
				double dist = player.getLocation().distanceSquared(origin);
				if (dist > 0)
					return;
				cp.flagHack(AntiKB1.this, 10);
			}
		}.runTaskLater(plugin, 5);
	}

	@Override
	public String getCategory() {
		return "AntiKB";
	}

	@Override
	public String getDebugName() {
		return "AntiKB#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	@Override
	public boolean onlyLegacy() {
		return false;
	}
}
