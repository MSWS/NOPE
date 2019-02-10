package org.mswsplex.anticheat.checks.render;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

public class Spinbot1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
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

		Location to = event.getTo(), from = event.getFrom();

		if (to.distanceSquared(from) == 0)
			return;

		double diff = to.getYaw() - from.getYaw();

		if (diff == 0)
			return;

		List<Double> yaws = (List<Double>) cp.getTempData("spinbotYaws");

		if (yaws == null)
			yaws = new ArrayList<>();

		yaws.add(0, diff);

		for (int i = SIZE; i < yaws.size(); i++) {
			yaws.remove(i);
		}

		cp.setTempData("spinbotYaws", yaws);

		if (yaws.size() < SIZE)
			return;

		int amo = yaws.stream().filter((val) -> val == diff).collect(Collectors.toList()).size();

		if (amo < SIZE / 2)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&c" + amo + " (" + diff + ")");

		cp.flagHack(this, (amo - SIZE / 2) * 5);
	}

	@Override
	public String getCategory() {
		return "Spinbot";
	}

	@Override
	public String getDebugName() {
		return "Spinbot#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
