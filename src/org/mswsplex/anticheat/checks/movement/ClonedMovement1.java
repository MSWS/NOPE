package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.List;

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

	private final int size = 20;

	@SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (cp.isInClimbingBlock())
			return;
		if (cp.isBlockNearby(Material.WEB))
			return;
		if (cp.timeSince("lastBlockPlace") < 500)
			return;

		if (player.isOnGround() && cp.isOnGround())
			return;

		Location from = event.getFrom(), to = event.getTo();

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		List<Double> distances = (ArrayList<Double>) cp.getTempData("teleportDistances");
		if (distances == null)
			distances = new ArrayList<>();

		int amo = 0;
		for (double d : distances) {
			if (d == 0)
				continue;
			if (d == dist)
				amo++;
		}

		distances.add(0, dist);

		for (int i = distances.size() - size; i < distances.size() && i > size; i++)
			distances.remove(i);

		cp.setTempData("teleportDistances", distances);

		if (amo < size / 4)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&9" + dist);

		cp.flagHack(this, 5);
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
