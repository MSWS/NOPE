package xyz.msws.nope.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
 * Checks recent movements, their differences, and flags if there are too many
 * duplicate differences
 * 
 * @author imodm
 *
 */
public class ClonedMovement1 implements Check, Listener {

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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;
		if (cp.isInClimbingBlock())
			return;
		if (cp.isBlockAbove())
			return;
		if (cp.isBlockNearby(Material.COBWEB))
			return;
		if (cp.timeSince(Stat.BLOCK_PLACE) < 500)
			return;
		if (cp.timeSince(Stat.TELEPORT) < 1000)
			return;

		if (player.isOnGround())
			return;

		Location from = event.getFrom(), to = event.getTo();

		if (cp.timeSince(Stat.HORIZONTAL_BLOCKCHANGE) > 500)
			return;

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		if (dist == 0)
			return;

		if (player.getLocation().getBlock().getType() == Material.COBWEB && dist <= 0.06586018003872596)
			return;

		List<Double> ds = distances.getOrDefault(player.getUniqueId(), new ArrayList<>());

		int amo = ds.stream().filter((val) -> val == dist).collect(Collectors.toList()).size();

		ds.add(0, dist);

		for (int i = distances.size() - SIZE; i < distances.size() && i > SIZE; i++)
			ds.remove(i);

		distances.put(player.getUniqueId(), ds);

		if (amo < SIZE / 4)
			return;

		cp.flagHack(this, (amo - (SIZE / 4)) * 2 + 5,
				"Dist: &e" + dist + " \n&7Similar: &e" + amo + "&7 >= &a" + (SIZE / 4));
	}

	@Override
	public String getCategory() {
		return "ClonedMovements";
	}

	@Override
	public String getDebugName() {
		return "ClonedMovement#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
