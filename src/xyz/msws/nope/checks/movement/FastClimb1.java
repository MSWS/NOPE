package xyz.msws.nope.checks.movement;

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
			0.37248415303335136, 0.38448416543112174, 0.1176000022888184, 0.11215904732696558, 0.1544480052490238,
			0.3724841530333496, 0.40488415670499567, 0.06456837797374515, 0.11032509974591953, 0.41578672560381413,
			0.3760841865550013, 0.4168841691027616, 0.11760000228881795, 0.11760000228881928, 0.1544480052490229,
			0.11760000228882461, 0.0368480029601983, 0.11215904732696202, 1.2595999415636072, 0.08320000648484438,
			0.0591999816894031, 0.39840002417564335, 0.11215904732696913, 0.4252000021934066, 0.1176000022888104 };

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince(Stat.DISABLE_FLIGHT) < 2000)
			return;

		if (!cp.isInClimbingBlock())
			return;

		if (cp.timeSince(Stat.ON_SLIMEBLOCK) < 1000)
			return;

		if (cp.timeSince(Stat.ON_GROUND) < 500)
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
