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
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Checks distance over certain amount of time
 * 
 * @author imodm
 *
 * @deprecated Just really bad
 */
public class Timer1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.TICK;
	}

	private Map<UUID, List<Long>> timings = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SAMPLE_SIZE = 500;

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isInsideVehicle())
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (cp.timeSince(Stat.FLYING) < 1000)
			return;

		if (cp.timeSince(Stat.ON_ICE) < 1000)
			return;

		List<Long> horizontalTimings = timings.getOrDefault(player.getUniqueId(), new ArrayList<>());

		if (cp.timeSince(Stat.TELEPORT) > 500)
			horizontalTimings.add(0, cp.timeSince(Stat.HORIZONTAL_BLOCKCHANGE));

		for (int i = SAMPLE_SIZE; i < horizontalTimings.size(); i++)
			horizontalTimings.remove(i);

		timings.put(player.getUniqueId(), horizontalTimings);

		if (horizontalTimings.size() < SAMPLE_SIZE)
			return;

		double avg = 0;

		for (double d : horizontalTimings)
			avg += d;

		avg /= horizontalTimings.size();

		if (avg > 85)
			return;

		horizontalTimings.add(0, cp.timeSince(Stat.HORIZONTAL_BLOCKCHANGE));
		timings.put(player.getUniqueId(), horizontalTimings);
		cp.flagHack(this, (int) Math.round((85 - avg)) * 2 + 5, "Avg: &e" + avg + "&7 <= &a85");
	}

	@Override
	public String getCategory() {
		return "Timer";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
