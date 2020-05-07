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
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks distance over certain amount of time
 * 
 * @author imodm
 *
 */
public class Timer2 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.TICK;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SAMPLE_SIZE = 150;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isInsideVehicle())
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (cp.timeSince("wasFlying") < 1000)
			return;
		
		if (cp.timeSince("lastOnIce") < 1000)
			return;

		List<Double> horizontalTimings = (List<Double>) cp.getTempData("timer2BlockTimings");

		if (horizontalTimings == null)
			horizontalTimings = new ArrayList<>();

		if (cp.timeSince("lastTeleport") > 500)
			horizontalTimings.add(0, cp.timeSince("lastHorizontalBlockChange"));

		for (int i = SAMPLE_SIZE; i < horizontalTimings.size(); i++)
			horizontalTimings.remove(i);

		cp.setTempData("timer2BlockTimings", horizontalTimings);

		if (horizontalTimings.size() < SAMPLE_SIZE)
			return;

		double avg = 0;

		for (double d : horizontalTimings)
			avg += d;

		avg /= horizontalTimings.size();

		if (avg > 85)
			return;

		horizontalTimings.add(0, cp.timeSince("lastHorizontalBlockChange"));
		cp.setTempData("timer2BlockTimings", horizontalTimings);
		cp.flagHack(this, (int) Math.round((85 - avg)) * 2 + 5, "Avg: &e" + avg + "&7 <= &a85");
	}

	@Override
	public String getCategory() {
		return "Timer";
	}

	@Override
	public String getDebugName() {
		return "Timer#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
