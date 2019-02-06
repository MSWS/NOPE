package org.mswsplex.anticheat.checks.render;

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

	private final int size = 20;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		Location to = event.getTo(), from = event.getFrom();

		double diff = to.getYaw() - from.getYaw();

		List<Double> yaws = (List<Double>) cp.getTempData("spinbotYaws");

		if (yaws == null)
			yaws = new ArrayList<>();

		yaws.add(0, diff);

		for (int i = size; i < yaws.size(); i++) {
			yaws.remove(i);
		}

		cp.setTempData("spinbotYaws", yaws);

		if (yaws.size() < size)
			return;

		int amo = 0;
		for (double d : yaws) {
			if (d == diff && d != 0)
				amo++;
		}

		if (amo < size / 2)
			return;

		MSG.tell(player, "&c" + amo + " (" + diff + ")");

		cp.flagHack(this, 5);
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
