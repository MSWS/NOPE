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
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

/**
 * 
 * Checks if the player's last ongrond position is too low and too far away
 * <i>conveniently</i> also checks Jesus
 * 
 * @author imodm
 * 
 */
public class Flight4 implements Check, Listener {

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

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || cp.timeSince("wasFlying") < 5000 || cp.isOnGround()
				|| cp.timeSince("lastTeleport") < 500 || cp.timeSince("lastFlightGrounded") < 500)
			return;

		Location safe = cp.getLastSafeLocation();

		double yDiff = safe.getY() - player.getLocation().getY();

		if (yDiff >= 0)
			return;

		double dist = safe.distanceSquared(player.getLocation());

		if (dist < 20)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&3dist: " + safe.distanceSquared(player.getLocation()) + " y: " + yDiff);

		cp.flagHack(this, Math.max(Math.min((int) Math.round((dist - 20) * 10.0), 50), 10));
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return "Flight#4";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
