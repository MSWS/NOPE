package org.mswsplex.anticheat.checks.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;
import org.mswsplex.anticheat.utils.MSG;

public class AutoSwitch1 implements Check, Listener {

	private final int SIZE = 100;

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onSwap(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> swaps = (List<Double>) cp.getTempData("SwapTimings");
		if (swaps == null)
			swaps = new ArrayList<>();

		swaps.add(0, (double) System.currentTimeMillis());
		if (swaps.size() > SIZE)
			swaps = swaps.subList(0, SIZE);

		cp.setTempData("SwapTimings", swaps);

		if (swaps.size() < SIZE)
			return;

		double avg = 0;

		double last = swaps.get(swaps.size() - 1);

		for (int i = swaps.size() - 2; i >= 0; i--) {
			MSG.tell(player, "&6" + (swaps.get(i) - last));
			avg += swaps.get(i) - last;
			last = swaps.get(i);
		}

		double deviation = 0;

		last = swaps.get(swaps.size() - 1);

		avg /= swaps.size();

		for (int i = swaps.size() - 2; i >= 0; i--) {
			deviation += Math.abs((swaps.get(i) - last) - avg);
			last = swaps.get(i);
		}

		MSG.tell(player, deviation + "");

		if (deviation > 20)
			return;
		cp.flagHack(this, (20 - (int) deviation) * 5, String.format("Avg: %.2f\nDeviation: %.2f", avg, deviation));
	}

	@Override
	public String getCategory() {
		return "AutoTool";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
