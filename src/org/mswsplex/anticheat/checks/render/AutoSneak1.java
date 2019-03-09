package org.mswsplex.anticheat.checks.render;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks how often a player sneaks
 * 
 * @author imodm
 *
 */
public class AutoSneak1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> sneakTimings = (List<Double>) cp.getTempData("autoSneakTimings");
		if (sneakTimings == null)
			sneakTimings = new ArrayList<>();

		sneakTimings.add((double) System.currentTimeMillis());

		sneakTimings = sneakTimings.stream().filter((val) -> System.currentTimeMillis() - val < 1000)
				.collect(Collectors.toList());

		cp.setTempData("autoSneakTimings", sneakTimings);

		if (sneakTimings.size() < 20)
			return;

		cp.flagHack(this, (sneakTimings.size() - 20) * 2, "Timings: &e" + sneakTimings.size() + "&7 >= &a20");
	}

	@Override
	public String getCategory() {
		return "AutoSneak";
	}

	@Override
	public String getDebugName() {
		return "AutoSneak#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	@Override
	public boolean onlyLegacy() {
		// TODO Auto-generated method stub
		return false;
	}
}
