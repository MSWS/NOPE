package xyz.msws.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Gets the average speed while a player is sneaking and flags if too high
 * 
 * @author imodm
 *
 */
public class FastSneak1 implements Check, Listener {

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

	private final int SIZE = 20;

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince(Stat.DISABLE_FLIGHT) < 2000)
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (!player.isSneaking())
			return;

		if (player.isSprinting())
			return;

		if (cp.timeSince(Stat.SPRINTING) < 200)
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE"))
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() != from.getY())
			return;

		Vector moveDiff = to.toVector().subtract(from.toVector()).normalize();
		Vector look = player.getLocation().getDirection();
		double diff = moveDiff.distanceSquared(look);
		if (diff > 2)
			return;

		double dist = from.distanceSquared(to);

		List<Double> ds = distances.getOrDefault(player.getUniqueId(), new ArrayList<>());

		double avg = 0;
		for (double d : ds)
			avg += d;

		avg /= distances.size();

		ds.add(0, dist);

		for (int i = distances.size() - SIZE; i < distances.size() && i > SIZE; i++)
			ds.remove(i);

		distances.put(player.getUniqueId(), ds);

		if (distances.size() < SIZE)
			return;

		double min = .0136;

		if (avg < min)
			return;

		cp.flagHack(this, (int) Math.round((avg / (min - min / 10)) * 20.0) + 5,
				"Average: &e" + avg + "&7 >= &a" + min + "\n&7Size: &e" + distances.size() + "&7 >= &a" + SIZE);
	}

	@Override
	public String getCategory() {
		return "FastSneak";
	}

	@Override
	public String getDebugName() {
		return "FastSneak#1";
	}

	@Override // Don't lag back because this can cause a few false flags
	public boolean lagBack() {
		return false;
	}
}
