package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;
import org.mswsplex.anticheat.utils.MSG;

/**
 * Checks if a player moves their yaw without changing their pitch
 * 
 * @author imodm
 *
 */
public class AntiAFK1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || player.isInsideVehicle())
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getYaw() == from.getYaw())
			return;

		if (to.getPitch() != from.getPitch())
			return;

		if (cp.timeSince("lastTeleport") < 5000)
			return;

		if (to.getPitch() != 0)
			return;

		List<Double> samePitchTimings = cp.getTempData("afkPitchTimings", List.class);
		if (samePitchTimings == null)
			samePitchTimings = new ArrayList<>();

		samePitchTimings.add((double) System.currentTimeMillis());

		Iterator<Double> it = samePitchTimings.iterator();
		while (it.hasNext()) {
			double d = it.next();
			if (System.currentTimeMillis() - d > 20000) {
				it.remove();
				continue;
			}
		}

		cp.setTempData("afkPitchTimings", samePitchTimings);

		if (samePitchTimings.size() < 25)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&6same: " + samePitchTimings.size());

		cp.flagHack(this, (samePitchTimings.size() - 25) * 3, "Moved with similar Yaw: &e" + samePitchTimings.size());
	}

	@Override
	public String getCategory() {
		return "AntiAFK";
	}

	@Override
	public String getDebugName() {
		return "AntiAFK#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
