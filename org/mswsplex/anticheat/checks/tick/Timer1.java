package org.mswsplex.anticheat.checks.tick;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class Timer1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.TICK;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 20, AVG_SIZE = 50;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> timings = (ArrayList<Double>) cp.getTempData("timerTimings");
		List<Integer> averageTimings = (ArrayList<Integer>) cp.getTempData("averageTimings");

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

				if (Math.round(lagTicks - avg) > 5) {
					if (plugin.devMode())
						MSG.tell(player,
								"&2Lag &a(avg: " + avg + " current: " + lagTicks + ") tps: " + plugin.getTPS());
					cp.flagHack(this, (int) (Math.round(lagTicks - avg) - 4) * 6);
				}
			}
			averageTimings.add(0, lagTicks);
			for (int i = AVG_SIZE; i < averageTimings.size(); i++)
				averageTimings.remove(i);
		}

		timings.add(0, (double) System.currentTimeMillis());

		for (int i = SIZE; i < timings.size(); i++)
			timings.remove(i);

		cp.setTempData("timerTimings", timings);
		cp.setTempData("averageTimings", averageTimings);
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
		return true;
	}
}
