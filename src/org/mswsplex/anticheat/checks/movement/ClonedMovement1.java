package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class ClonedMovement1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 20;

	@SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;
		if (cp.isInClimbingBlock())
			return;
		if (cp.isBlockNearby(Material.WEB))
			return;
		if (cp.timeSince("lastBlockPlace") < 500)
			return;
		if (cp.timeSince("lastTeleported") < 1000)
			return;

		if (player.isOnGround() && cp.isOnGround())
			return;

		Location from = event.getFrom(), to = event.getTo();

		if (cp.timeSince("lastHorizontalBlockChange") > 500)
			return;

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		if (dist == 0)
			return;

		if (player.getLocation().getBlock().getType() == Material.WEB && dist <= 0.06586018003872596)
			return;

		List<Double> distances = (ArrayList<Double>) cp.getTempData("teleportDistances");
		if (distances == null)
			distances = new ArrayList<>();

		int amo = distances.stream().filter((val) -> val == dist).collect(Collectors.toList()).size();

		distances.add(0, dist);

		for (int i = distances.size() - SIZE; i < distances.size() && i > SIZE; i++)
			distances.remove(i);

		cp.setTempData("teleportDistances", distances);

		if (amo < SIZE / 4)
			return;

		if (plugin.devMode()) {
			MSG.tell(player, "&7lastblock: &3" + cp.timeSince("lastHorizontalBlockChange"));
			MSG.tell(player, "&9" + dist);
		}

		cp.flagHack(this, (amo - (SIZE / 4)) * 2 + 5);
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
