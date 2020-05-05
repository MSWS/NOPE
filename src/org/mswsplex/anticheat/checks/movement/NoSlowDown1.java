package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
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

/**
 * Checks the average speed of a player while they're blocking and on the ground
 * 
 * @author imodm
 *
 */
public class NoSlowDown1 implements Check, Listener {

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

	private final int SIZE = 40;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;

		if (!player.isBlocking())
			return;

		if (!cp.isOnGround())
			return;

		if (cp.timeSince("lastLiquid") < 500)
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		List<Double> distances = (ArrayList<Double>) cp.getTempData("noSlowDistances");
		if (distances == null)
			distances = new ArrayList<>();

		double avg = 0;
		for (double d : distances)
			avg += d;

		avg /= distances.size();

		distances.add(0, dist);

		for (int i = distances.size() - SIZE; i < distances.size() && i > SIZE; i++)
			distances.remove(i);

		cp.setTempData("noSlowDistances", distances);

		if (distances.size() < SIZE)
			return;

		if (avg <= .176)
			return;

		cp.flagHack(this, (int) Math.round((avg - .16) * 400.0), "Average: &e" + avg + ">&a.176");
	}

	@Override
	public String getCategory() {
		return "NoSlowDown";
	}

	@Override
	public String getDebugName() {
		return "NoSlowDown#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
