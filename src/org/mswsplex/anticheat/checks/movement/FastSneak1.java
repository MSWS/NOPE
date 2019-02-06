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

public class FastSneak1 implements Check, Listener {

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

	private final int size = 20;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;

		if (!player.isSneaking())
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = from.distanceSquared(to);

		List<Double> distances = (ArrayList<Double>) cp.getTempData("sneakDistances");
		if (distances == null)
			distances = new ArrayList<>();

		double avg = 0;
		for (double d : distances)
			avg += d;

		avg /= distances.size();

		distances.add(0, dist);

		for (int i = distances.size() - size; i < distances.size() && i > size; i++)
			distances.remove(i);

		cp.setTempData("sneakDistances", distances);

		if (distances.size() < size)
			return;

		if (avg < .08)
			return;

		MSG.tell(player, "&e" + avg);

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "FastSneak";
	}

	@Override
	public String getDebugName() {
		return "FastSneak#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
