package xyz.msws.anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * 
 * @author imodm
 * 
 * @deprecated
 */
public class KillAura4 implements Check, Listener {

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

		if (yawOffset < 2)
			return;

		cp.flagHack(this, (int) ((yawOffset - 1.3) * 10), String.format("Yaw Diff: &e%.3f", yawOffset));
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#4";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
