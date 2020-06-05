package xyz.msws.nope.checks.movement.speed;

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
		if (player.isFlying() || player.isInsideVehicle() || player.isGliding())
			return;

		if (cp.timeSince(Stat.FLYING) < 2000)
			return;
		if (cp.timeSince(Stat.ICE_TRAPDOOR) < 1000)
			return;
		if (cp.timeSince(Stat.LEAVE_VEHICLE) < 500)
			return;
		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 200)
			return;
		if (cp.timeSince(Stat.ON_SLIMEBLOCK) < 1000)
			return;
		if (cp.timeSince(Stat.DISABLE_GLIDE) < 2000)
			return;
		if (cp.timeSince(Stat.REDSTONE) < 1000)
			return;
		if (cp.hasMovementRelatedPotion())
			return;
		if (player.getFallDistance() > 4)
			return;
		if (player.getLocation().getBlock().isLiquid())
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
