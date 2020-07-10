package xyz.msws.nope.checks.movement.speed;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks if a player is sprinting in the direction that they aren't looking in
 * 
 * @author imodm
 *
 */
public class Speed4 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince(Stat.FLYING) < 1000)
			return;

		if (cp.timeSince(Stat.DISABLE_FLIGHT) < 2000)
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE"))
			return;

		Location to = event.getTo(), from = event.getFrom();

		Vector moveDiff = to.toVector().subtract(from.toVector());
		Vector look = player.getLocation().getDirection();

		moveDiff.setY(0);
		look.setY(0);
		look.normalize();
		moveDiff.normalize();

		double diff = moveDiff.distanceSquared(look);

		if ((diff + "").contains("0000000000")) {
			cp.flagHack(this, 20);
		}
	}

	@Override
	public String getCategory() {
		return "Speed";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#5";
	}

	@Override
	public boolean lagBack() {
		return true;
	}

}
