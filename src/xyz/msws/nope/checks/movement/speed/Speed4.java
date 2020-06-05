package xyz.msws.nope.checks.movement.speed;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks instantaneous speeds
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
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || player.isGliding())
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 500)
			return;

		if (cp.timeSince(Stat.MOVE) < 500)
			return;

		if (cp.timeSince(Stat.TOGGLE_GLIDE) < 500)
			return;

		if (cp.timeSince(Stat.IN_VEHICLE) < 2000)
			return;

		double maxDist = 0.02500431987726535;

		Location to = event.getTo().clone(), from = event.getFrom().clone();
		to.setY(0);
		from.setY(0);

		double dist = to.distanceSquared(from);

		if (dist <= maxDist)
			return;

		cp.flagHack(this, (int) ((dist - maxDist) * 100) + 10, "Dist: " + dist + " > " + maxDist);
	}

	@Override
	public String getCategory() {
		return "Speed";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#4";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
