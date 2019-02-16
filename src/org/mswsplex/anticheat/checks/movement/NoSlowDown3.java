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
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

/**
 * Checks the average speed of a player while they're blocking either in air or
 * on ground
 * 
 * @author imodm
 *
 */
public class NoSlowDown3 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(AntiCheat plugin) {
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

		if (cp.timeSince("lastLiquid") < 500)
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		List<Double> distances = (ArrayList<Double>) cp.getTempData("noSlowAirDistances");
		if (distances == null)
			distances = new ArrayList<>();

		double avg = 0;
		for (double d : distances)
			avg += d;

		avg /= distances.size();

		distances.add(0, dist);

		for (int i = distances.size() - SIZE; i < distances.size() && i > SIZE; i++)
			distances.remove(i);

		cp.setTempData("noSlowAirDistances", distances);

		if (distances.size() < SIZE)
			return;

		if (avg <= .43)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&2" + avg);

		cp.flagHack(this, (int) Math.round((avg - .29) * 400.0));
	}

	@Override
	public String getCategory() {
		return "NoSlowDown";
	}

	@Override
	public String getDebugName() {
		return "NoSlowDown#3";
	}

	@Override
	public boolean lagBack() {
		return true;
	}

	@Override
	public boolean onlyLegacy() {
		// TODO Auto-generated method stub
		return false;
	}
}
