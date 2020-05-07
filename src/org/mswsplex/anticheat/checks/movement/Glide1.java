package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks if a player's fall velocity is less than the player's previous fall
 * velocity
 * 
 * @author imodm
 *
 */
public class Glide1 implements Check, Listener {

	private final int SIZE = 10;

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

		if (player.isOnGround() || player.isFlying())
			cp.removeTempData("FallDistances");

		if (cp.isInClimbingBlock() || cp.isInWeirdBlock() || player.isFlying() || player.isOnGround())
			return;

		if (cp.timeSince("wasFlying") < 500)
			return;

		if (cp.timeSince("lastOnGround") < 500 || cp.timeSince("lastFlightGrounded") < 500)
			return;

		if (cp.timeSince("lastLiquid") < 500)
			return;

		double fallDist = event.getFrom().getY() - event.getTo().getY();

		if (fallDist == 0 || player.getFallDistance() == 0) {
			cp.removeTempData("previousFall");
			return;
		}

		if (!cp.hasTempData("previousFall")) {
			cp.setTempData("previousFall", fallDist);
			return;
		}

		double previousFall = cp.getTempDouble("previousFall");
		double diff = fallDist - previousFall;

		List<Double> fallDistances = (List<Double>) cp.getTempData("FallDistances");
		if (fallDistances == null)
			fallDistances = new ArrayList<>();

		fallDistances.add(0, diff);

		if (fallDistances.size() > SIZE) {
			fallDistances = fallDistances.subList(0, SIZE);
		}

		cp.setTempData("FallDistances", fallDistances);

		if (fallDistances.size() < SIZE)
			return;

		double avg = 0;
		for (double d : fallDistances)
			avg += d;

		avg /= fallDistances.size();

		if (avg > 0)
			return;

		cp.flagHack(this, (int) ((2 - avg) * 50) + 10, "&7Avg: &e" + avg);
	}

	@Override
	public String getCategory() {
		return "Glide";
	}

	@Override
	public String getDebugName() {
		return "Glide#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
