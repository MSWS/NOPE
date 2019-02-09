package org.mswsplex.anticheat.checks.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Scaffold2 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
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
		List<Double> avgPitches = (List<Double>) cp.getTempData("averageScaffoldPitches");
		if (avgPitches == null)
			avgPitches = new ArrayList<>();

		avgPitches.add(0, (double) player.getLocation().getPitch());

		for (int i = size; i < avgPitches.size(); i++) {
			avgPitches.remove(i);
		}

		cp.setTempData("averageScaffoldPitches", avgPitches);

	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> avgPitches = (List<Double>) cp.getTempData("averageScaffoldPitches");
		if (avgPitches == null)
			return;
		if (avgPitches.size() < size)
			return;

		if (event.getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
			return;

		double total = 0;
		for (double d : avgPitches)
			total += d;
		total /= avgPitches.size();

		double diff = Math.abs((player.getLocation().getPitch() - total));

		if (diff < 30)
			return;

		cp.flagHack(this, (int) Math.round((diff - 30) / 5));
	}

	@Override
	public String getCategory() {
		return "Scaffold";
	}

	@Override
	public String getDebugName() {
		return "Scaffold#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
