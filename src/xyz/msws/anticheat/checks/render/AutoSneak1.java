package xyz.msws.anticheat.checks.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

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

	private Map<UUID, List<Double>> timings = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> sneakTimings = timings.getOrDefault(player.getUniqueId(), new ArrayList<>());

		sneakTimings.add((double) System.currentTimeMillis());

		sneakTimings = sneakTimings.stream().filter((val) -> System.currentTimeMillis() - val < 1000)
				.collect(Collectors.toList());

		timings.put(player.getUniqueId(), sneakTimings);

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
}
