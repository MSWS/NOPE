package org.mswsplex.anticheat.checks.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

/**
 * Compares yaw rates right before a player hits an entity
 * 
 * @author imodm
 *
 */
public class KillAura2 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(AntiCheat plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		double yawDiff = Math.abs(Math.abs(event.getTo().getYaw()) - Math.abs(event.getFrom().getYaw()));

		if (yawDiff < 30)
			return;

		List<Double> fastTimings = (List<Double>) cp.getTempData("killAuraYawTimings");
		if (fastTimings == null)
			fastTimings = new ArrayList<>();

		fastTimings.add((double) System.currentTimeMillis());

		Iterator<Double> it = fastTimings.iterator();

		while (it.hasNext()) {
			double val = it.next();
			if (System.currentTimeMillis() - val > 250) {
				it.remove();
			}
		}
		cp.setTempData("killAuraYawTimings", fastTimings);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> fastTimings = (List<Double>) cp.getTempData("killAuraYawTimings");
		if (fastTimings == null)
			return;

		Iterator<Double> it = fastTimings.iterator();

		while (it.hasNext()) {
			double val = it.next();
			if (System.currentTimeMillis() - val > 250) {
				it.remove();
			}
		}

		if (fastTimings.size() <= 4)
			return;

		cp.flagHack(this, 50, "Rotated too quickly: &e" + fastTimings.size());
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return "KillAura#2";
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
