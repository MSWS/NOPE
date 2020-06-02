package xyz.msws.anticheat.checks.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Checks if yaw differences is constant
 * 
 * @author imodm
 *
 */
public class Spinbot1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	private Map<UUID, List<Double>> yaws = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 40;

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

		List<Double> ys = yaws.getOrDefault(player.getUniqueId(), new ArrayList<>());

		if (ys == null)
			ys = new ArrayList<>();

		ys.add(0, diff);

		for (int i = SIZE; i < ys.size(); i++) {
			ys.remove(i);
		}

//		cp.setTempData("spinbotYaws", ys);
		yaws.put(player.getUniqueId(), ys);

		if (ys.size() < SIZE)
			return;

		int amo = ys.stream().filter((val) -> val == diff).collect(Collectors.toList()).size();

		if (amo < SIZE / 2)
			return;

		cp.flagHack(this, (amo - SIZE / 2) * 5, "Amo: &e" + amo + "&7>=" + (SIZE / 2) + "\nDiff: &e" + diff);
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
