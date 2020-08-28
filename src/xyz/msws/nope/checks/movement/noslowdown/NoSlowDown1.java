package xyz.msws.nope.checks.movement.noslowdown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks the average speed of a player while they're blocking and on the ground
 * 
 * @author imodm
 *
 */
public class NoSlowDown1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private Map<UUID, List<Double>> distances = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 40;

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince(Stat.DISABLE_FLIGHT) < 2000)
			return;

		if (!player.isBlocking())
			return;

		if (!player.isOnGround())
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		List<Double> ds = distances.getOrDefault(player.getUniqueId(), new ArrayList<>());

		double avg = 0;
		for (double d : ds)
			avg += d;

		avg /= distances.size();

		ds.add(0, dist);

		for (int i = ds.size() - SIZE; i < ds.size() && i > SIZE; i++)
			ds.remove(i);

		distances.put(player.getUniqueId(), ds);

		if (distances.size() < SIZE)
			return;

		if (avg <= .176)
			return;

		cp.flagHack(this, (int) Math.round((avg - .16) * 400.0), "Average: &e" + avg + ">&a.176");
	}

	@Override
	public String getCategory() {
		return "NoSlowDown";
	}

	@Override
	public String getDebugName() {
		return "NoSlowDown#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
