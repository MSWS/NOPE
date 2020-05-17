package xyz.msws.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * 
 * Checks if the player's last ongrond position is too low and too far away
 * <i>conveniently</i> also checks Jesus
 * 
 * @author imodm
 * 
 */
public class Flight6 implements Check, Listener {

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

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (!player.isInsideVehicle())
			return;

		Entity vehicle = player.getVehicle();
		if (vehicle.isOnGround())
			cp.setTempData("lastVehicleGrounded", (double) System.currentTimeMillis());
		if (player.getLocation().getBlock().isLiquid())
			return;
		double yDiff = event.getTo().getY() - event.getFrom().getY();

		if (yDiff < 0) {
			if (cp.timeSince("lastVehicleGrounded") < 1000)
				return;
			if (yDiff < -.1)
				return;
			cp.flagHack(this, 10);
			return;
		}

		if (player.getLocation().clone().add(0, -1, 0).getBlock().isLiquid())
			return;

		if (cp.distanceToGround() < 5)
			return;

		cp.flagHack(this, 20);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#6";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
