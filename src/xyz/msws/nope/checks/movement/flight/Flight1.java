package xyz.msws.nope.checks.movement.flight;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
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
 * Checks if a player moves completely horizontally without being on the ground
 * 
 * @author imodm
 *
 */
public class Flight1 implements Check, Listener {

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

		if (player.isOnGround())
			return;
		if (player.isFlying() || cp.isInClimbingBlock() || player.isInsideVehicle() || player.isRiptiding())
			return;
		if (cp.timeSince(Stat.FLYING) < 500)
			return;
		if (cp.timeSince(Stat.IN_LIQUID) < 400)
			return;
		if (cp.timeSince(Stat.RESPAWN) < 1000)
			return;

		if (player.getNearbyEntities(2, 3, 2).stream().anyMatch(e -> e.getType() == EntityType.BOAT))
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() != from.getY())
			return;

		if (cp.timeSince(Stat.COBWEB) < 500)
			return;

		if (cp.timeSince(Stat.TELEPORT) < 500)
			return;

		if (cp.timeSince(Stat.ON_GROUND) <= 300)
			return;
		if (cp.timeSince(Stat.BLOCK_PLACE) < 1500)
			return;
		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 2000)
			return;

		if (player.getVelocity().getY() > 0)
			return;

		cp.flagHack(this, 30);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return "Flight#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
