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

public class BunnyHop1 implements Check, Listener {

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

	private final int size = 15;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;
		if (cp.hasMovementRelatedPotion())
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		List<Double> distances = (List<Double>) cp.getTempData("bunnyHopDistances");
		if (distances == null)
			distances = new ArrayList<>();

		distances.add(0, dist);

		for (int i = size; i < distances.size(); i++) {
			distances.remove(i);
		}

		cp.setTempData("bunnyHopDistances", distances);

		if (distances.size() < size)
			return;

		double avg = 0;

		for (double d : distances)
			avg += d;

		avg /= distances.size();

		if (avg < .53)
			return;

		MSG.tell(player, "&2" + avg);

		cp.flagHack(this, 10);
	}

	@Override
	public String getCategory() {
		return "BunnyHop";
	}

	@Override
	public String getDebugName() {
		return "BunnyHop#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
