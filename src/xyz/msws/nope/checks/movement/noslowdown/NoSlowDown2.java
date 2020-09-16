package xyz.msws.nope.checks.movement.noslowdown;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks the player's speed in a snapshot of time while on ground
 * 
 * @author imodm
 *
 */
public class NoSlowDown2 implements Check, Listener {

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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (!player.isOnGround())
			return;

		if (!player.isBlocking())
			return;
		
		if (cp.timeSince(Stat.DISABLE_FLIGHT) < 2000)
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		Location to = event.getTo(), from = event.getFrom();

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		if (dist < .39)
			return;

		cp.flagHack(this, (int) Math.round((dist - .29) * 400.0), "Dist: &e" + dist + "&7 >= &a.39");
	}

	@Override
	public String getCategory() {
		return "NoSlowDown";
	}

	@Override
	public String getDebugName() {
		return "NoSlowDown#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
