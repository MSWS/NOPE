package xyz.msws.nope.checks.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

public class AutoSwitch1 implements Check, Listener {

	private final int SIZE = 100;

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private Map<UUID, List<Double>> swaps = new HashMap<>();

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSwap(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> st = swaps.getOrDefault(player.getUniqueId(), new ArrayList<>());

		st.add(0, (double) System.currentTimeMillis());
		if (st.size() > SIZE)
			st = st.subList(0, SIZE);

//		cp.setTempData("SwapTimings", st);
		swaps.put(player.getUniqueId(), st);

		if (st.size() < SIZE)
			return;

		double avg = 0;

		double last = st.get(st.size() - 1);

		for (int i = st.size() - 2; i >= 0; i--) {
			avg += st.get(i) - last;
			last = st.get(i);
		}

		double deviation = 0;

		last = st.get(st.size() - 1);

		avg /= st.size();

		for (int i = st.size() - 2; i >= 0; i--) {
			deviation += Math.abs((st.get(i) - last) - avg);
			last = st.get(i);
		}

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
