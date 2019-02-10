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
 * Checks average speeds
 * 
 * @author imodm
 *
 */
public class Speed3 implements Check, Listener {

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

	private final int SIZE = 15;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;
		if (cp.timeSince("iceAndTrapdoor") < 1000)
			return;
		if (cp.hasMovementRelatedPotion())
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		List<Double> distances = (List<Double>) cp.getTempData("speedDistances");
		if (distances == null)
			distances = new ArrayList<>();

		distances.add(0, dist);

		for (int i = SIZE; i < distances.size(); i++) {
			distances.remove(i);
		}

		cp.setTempData("speedDistances", distances);

		if (distances.size() < SIZE)
			return;

		double avg = 0;

		for (double d : distances)
			avg += d;

		avg /= distances.size();

		if (avg < .53)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&2" + avg);

		cp.flagHack(this, (int) Math.round((avg - .53) * 20));
	}

	@Override
	public String getCategory() {
		return "Speed";
	}

	@Override
	public String getDebugName() {
		return "Speed#3";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
