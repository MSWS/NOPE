package xyz.msws.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.checks.Global.Stat;
import xyz.msws.anticheat.data.CPlayer;

public class GlobalSprint1 implements Check, Listener {

	private final int SIZE = 100;

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Map<UUID, List<Double>> distances = new HashMap<UUID, List<Double>>();

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

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE"))
			return;

		Location to = event.getTo(), from = event.getFrom();

		Vector moveDiff = to.toVector().subtract(from.toVector());
		Vector look = player.getLocation().getDirection();
		Location flatTo = to.clone(), flatFrom = from.clone();
		flatTo.setY(0);
		flatFrom.setY(0);

		moveDiff.setY(0);
		look.setY(0);
		look.normalize();
		moveDiff.normalize();

		double diff = moveDiff.distanceSquared(look);
		double dist = flatTo.distanceSquared(flatFrom);

		if (diff < .5)
			return;
//		MSG.tell(player, "&a" + diff);

		List<Double> ds = distances.getOrDefault(player.getUniqueId(), new ArrayList<Double>());
		ds.add(0, dist);
		ds.subList(0, Math.min(ds.size(), SIZE));
		distances.put(player.getUniqueId(), ds);

		double avg = 0;
		for (double d : ds)
			avg += d;

		avg /= ds.size();

		if (avg < .5)
			return;

		cp.flagHack(this, (int) ((avg - .5) * 20), "Avg: &e" + avg);
	}

	@Override
	public String getCategory() {
		return "GlobalSprint";
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
