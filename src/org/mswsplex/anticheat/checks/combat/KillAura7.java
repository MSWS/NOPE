package org.mswsplex.anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * 
 * @author imodm
 * 
 *
 */
public class KillAura7 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		Entity target = event.getEntity();

		Location loc = player.getLocation();

		Vector offset = player.getLocation().toVector()
				.add(loc.getDirection().normalize().multiply(loc.distance(target.getLocation())));

		double yawOffset = offset.clone().setY(target.getLocation().getY())
				.distanceSquared(target.getLocation().toVector());

		if (yawOffset < .3)
			return;

		cp.flagHack(this, (int) ((yawOffset - .3) * 10), String.format("Yaw Diff: &e%.3f", yawOffset));
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#7";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
