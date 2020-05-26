package xyz.msws.anticheat.checks.movement.jesus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.checks.Global.Stat;
import xyz.msws.anticheat.data.CPlayer;

/**
 * 
 * @author imodm
 *
 */
public class Jesus1 implements Check, Listener {

	private NOPE plugin;

	private final int SIZE = 20;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Map<UUID, List<Double>> yDiffs = new HashMap<UUID, List<Double>>();

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isSwimming() || player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince(Stat.FLYING) < 3000)
			return;

		if (player.getLocation().clone().add(0, 2, 0).getBlock().isLiquid())
			return;

		if (player.getNearbyEntities(2, 3, 2).stream().anyMatch(e -> e.getType() == EntityType.BOAT))
			return;

		Location vertLine = player.getLocation();
		while (vertLine.getBlock().getType() == Material.AIR && vertLine.getY() > 0) {
			vertLine.subtract(0, 1, 0);
		}

		double dst = (player.getLocation().getY() - vertLine.getBlockY() - 1) + player.getLocation().getY() % 1;

		if (dst > 3)
			return;

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Block b = player.getLocation().clone().add(x, -.09375, z).getBlock();
				if (b.getType().isSolid() || b.getType() == Material.LILY_PAD)
					return;
				for (int y = -1; y <= 0; y++) {
					b = player.getLocation().clone().add(x, y, z).getBlock();
					if (b.getType().isSolid()) {
						yDiffs.remove(player.getUniqueId());
						return;
					}
				}
			}
		}

		if (!player.getLocation().subtract(0, dst + 1, 0).getBlock().isLiquid())
			return;

		List<Double> diffs = yDiffs.getOrDefault(player.getUniqueId(), new ArrayList<Double>());

		diffs.add(0, dst);
		diffs = diffs.subList(0, Math.min(SIZE, diffs.size()));

		yDiffs.put(player.getUniqueId(), diffs);

		if (diffs.size() < SIZE)
			return;

		double avg = 0;
		for (double d : diffs)
			avg += d;

		avg /= diffs.size();

		double avv = 0;
		for (double d : diffs)
			avv += Math.abs(avg - d);

		avv /= diffs.size();

		if (avv > .005 && avg < 1 && avg != avv)
			return;

		cp.flagHack(this, 10, "Average: &e" + avg + "\n&7AVD: &a" + avv);

	}

	@Override
	public String getCategory() {
		return "Jesus";
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
