package xyz.msws.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * Checks jumping speed
 * 
 * @author imodm
 *
 */
public class Speed2 implements Check, Listener {

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
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;
		if (cp.timeSince("iceAndTrapdoor") < 1000)
			return;
		if (cp.timeSince("leaveVehicle") < 100)
			return;
		if (cp.timeSince("lastDamageTaken") < 200)
			return;
		if (cp.hasMovementRelatedPotion())
			return;
		if (player.getFallDistance() > 4)
			return;
		if (cp.usingElytra())
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = to.distanceSquared(from);

		if (dist < .7)
			return;

		cp.flagHack(this, (int) Math.round((dist - .7) * 20), "Dist: &e" + dist + "&7 >= &a.7");
	}

	@Override
	public String getCategory() {
		return "Speed";
	}

	@Override
	public String getDebugName() {
		return "Speed#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
