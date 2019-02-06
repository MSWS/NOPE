package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Jesus1 implements Check, Listener {

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

	private final int size = 50;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || player.isInsideVehicle())
			return;

		Location to = event.getTo(), from = event.getFrom();

		boolean groundAround = false, liquidAround = false;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (player.getLocation().clone().add(x, -.5, z).getBlock().getType().isSolid())
					groundAround = true;
				if (player.getLocation().clone().add(x, -.5, z).getBlock().isLiquid())
					liquidAround = true;
				if (groundAround && liquidAround)
					break;
			}
		}

		if (!liquidAround || groundAround)
			return;

		double diff = to.getY() - from.getY();

		List<Double> lastDiffs = (List<Double>) cp.getTempData("jesusDiffs");
		if (lastDiffs == null)
			lastDiffs = new ArrayList<>();

		if (diff != -0.10000000596046732 && diff != 0.10000000149011612)
			lastDiffs.add(0, diff);

		for (int i = size; i < lastDiffs.size(); i++) {
			lastDiffs.remove(i);
		}

		int amo = 0;
		for (double d : lastDiffs) {
			if (d == diff)
				amo++;
		}

		cp.setTempData("jesusDiffs", lastDiffs);

		if (amo < size / 5)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "Jesus";
	}

	@Override
	public String getDebugName() {
		return "Jesus#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
