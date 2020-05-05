package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks Y differences and flags if they aren't <i>normal</i>
 * 
 * @author imodm
 *
 */
public class FastClimb1 implements Check, Listener {

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

	private double[] requires = { 0.0, 0.1176000022888175, 0.036848002960205406, 0.41999998688697815,
			0.37248415303335136, 0.38448416543112174 };

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;

		if (!cp.isInClimbingBlock())
			return;

		if (cp.timeSince("lastSlimeBlock") < 1000)
			return;

		if (cp.timeSince("lastOnGround") < 100)
			return;

		Location to = event.getTo();
		double dist = to.getY() - event.getFrom().getY();

		boolean normal = false;

		if (dist <= 0)
			return;

		for (double d : requires) {
			if (d == dist) {
				normal = true;
				break;
			}
		}

		if (normal)
			return;
		cp.flagHack(this, 15, "Dist: &e" + dist);
	}

	@Override
	public String getCategory() {
		return "FastClimb";
	}

	@Override
	public String getDebugName() {
		return "FastClimb#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
