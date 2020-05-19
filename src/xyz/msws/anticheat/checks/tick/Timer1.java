package xyz.msws.anticheat.checks.tick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * Checks average "lag ticks" and flags erroneous behavior
 * 
 * @author imodm
 *
 */
public class Timer1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.TICK;
	}

	private Map<UUID, List<Double>> rawTimings = new HashMap<>();
	private Map<UUID, List<Integer>> avgTimings = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 20, AVG_SIZE = 50;

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> timings = rawTimings.getOrDefault(player.getUniqueId(), new ArrayList<>());
		List<Integer> averageTimings = avgTimings.getOrDefault(player.getUniqueId(), new ArrayList<>());

		if (timings == null)
			timings = new ArrayList<>();

		if (averageTimings == null)
			averageTimings = new ArrayList<>();

		int lagTicks = 0;

		if (timings.size() >= SIZE) {
			double last = System.currentTimeMillis();
			for (double d : timings) {
				double diff = last - d;
				if (diff == 0)
					lagTicks++;
				last = d;
			}
			if (averageTimings.size() >= AVG_SIZE) {
				double avg = 0;
				for (double time : averageTimings)
					avg += time;
				avg /= averageTimings.size();

				if (Math.round(lagTicks - avg) > 6) {
					cp.flagHack(this, (int) (Math.round(lagTicks - avg) - 5) * 2, "&7Lag\n&7 Avg: &e" + avg
							+ "\n&7 Current: &e" + lagTicks + "\n\n&7TPS: &e" + plugin.getTPS());
				}
			}
			averageTimings.add(0, lagTicks);
			for (int i = AVG_SIZE; i < averageTimings.size(); i++)
				averageTimings.remove(i);
		}

		timings.add(0, (double) System.currentTimeMillis());

		for (int i = SIZE; i < timings.size(); i++)
			timings.remove(i);

		rawTimings.put(player.getUniqueId(), timings);
		avgTimings.put(player.getUniqueId(), averageTimings);
	}

	@Override
	public String getCategory() {
		return "Timer";
	}

	@Override
	public String getDebugName() {
		return "Timer#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
