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
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks if a player's Y differences are too similar
 * 
 * @author imodm
 * 
 *         TODO rewrite - buggy while a player is jumping on lilypads doesn't do
 *         average, merely counts up until max size, very unintentional and
 *         poorly written
 *
 */
public class Jesus1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 50;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || player.isInsideVehicle() || player.isSwimming())
			return;
//		if (player.getLocation().getBlock().isLiquid())
//			return;
		if (player.getLocation().getBlock().getType() == Material.LAVA)
			return;

		if (player.isOnGround())
			return;

		Location to = event.getTo(), from = event.getFrom();

		boolean liquidAround = false;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (player.getLocation().clone().add(x, -.5, z).getBlock().isLiquid())
					liquidAround = true;
				if (player.getLocation().clone().add(x, -.5, z).getBlock().getType().isSolid())
					return;
				if (liquidAround)
					break;
			}
		}

		if (!liquidAround)
			return;

		double diff = to.getY() - from.getY();

		List<Double> lastDiffs = (List<Double>) cp.getTempData("jesusDiffs");
		if (lastDiffs == null)
			lastDiffs = new ArrayList<>();

		if (diff != -0.10000000596046732 && diff != 0.10000000149011612)
			lastDiffs.add(0, diff);

		for (int i = SIZE; i < lastDiffs.size(); i++) {
			lastDiffs.remove(i);
		}

		int amo = lastDiffs.stream().filter((val) -> diff == val).collect(Collectors.toList()).size();

		cp.setTempData("jesusDiffs", lastDiffs);

		if (amo < SIZE / 5)
			return;

		cp.flagHack(this, (amo - (SIZE / 5)) * 10 + 5, "Amo: &e" + amo + "&7 >= &a" + (SIZE / 5));
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
