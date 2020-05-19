package xyz.msws.anticheat.checks.movement;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.google.common.collect.Sets;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.checks.Global.Stat;
import xyz.msws.anticheat.data.CPlayer;

/**
 * Checks Y differences and flags if they aren't <i>normal</i>
 * 
 * @author imodm
 * 
 * @deprecated
 *
 */
public class Step1 implements Check, Listener {

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

		if (player.isFlying() || cp.hasMovementRelatedPotion())
			return;

		if (player.getFallDistance() > 5)
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		if (cp.timeSince(Stat.ON_SLIMEBLOCK) < 1000)
			return;

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 500)
			return;

		if (cp.timeSince(Stat.IN_VEHICLE) < 1000)
			return;

		if (cp.timeSince(Stat.TOGGLE_GLIDE) < 500)
			return;

		if (cp.timeSince(Stat.BLOCK_PLACE) < 500)
			return;

		if (cp.timeSince(Stat.TELEPORT) < 500)
			return;

		if (cp.timeSince(Stat.TOGGLE_FLIGHT) < 1000)
			return;

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 1500)
			return;

		if (cp.timeSince(Stat.CLIMBING) < 1000)
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() == from.getY())
			return;

		if (!player.isOnGround() || cp.isInWeirdBlock())
			return;

		if (cp.isBlockAbove() && cp.distanceToGround() < 2)
			return;

		if (player.getNearbyEntities(1, 3, 1).stream().anyMatch(e -> e.getType() == EntityType.BOAT))
			return;

		double diff = to.getY() - from.getY();

		if (diff < -.5)
			return;

		if (regular.contains(diff))
			return;

		cp.flagHack(this, 20, "Diff: &e" + diff);
	}

	private HashSet<Double> regular = Sets.newHashSet(0.41999998688697815, -0.015555072702198913, -0.07840000152587834,
			0.2000000476837016, 0.20000004768311896, 0.12160004615724063, 0.20000004768371582, 0.20000004768371582,
			0.2000000476836732, 0.20000004768365898, -0.07840000152587923, -0.07840000152587878, -0.35489329934835556,
			-0.10408037809303572, 0.40444491418477835, -.6517088341626156, .033890786745502055, -0.1216000461578366,
			-0.05489334703207138, 0.20000004768370871, 0.6000000238418579, -0.12129684053918766, -.02442408821369213,
			-.12129684053920187, -.12129684053918943, -.12129684053919121, -.024424088213679696, -.1621414210269867,
			-.12129684053918988, -0.024424088213680584, -.1260004615783748, -.12160004615783748,

			// Falling
			-0.15552175294311255, -.1621414210269876, -.02442408821367792, -.23152379758701258, -.4404551304719586,
			-.21589434553872255, -.1621414210270018, -.23152379758701613,

			// Slab interactions
			.5, -0.03584062504455687, -0.6517088341626174, -0.0358406250445551, -0.6537296175885947,
			-0.1537296175885947, .375, -.18379684053918943, -.0625, .0625, -.17647087614426837, -.09004684053918766,
			-.21504684053918766, -.058796840539187656, -.18379684053918766, -0.2315237975870108, -0.39044745325996466,
			-0.03584062504455554, 0.46415937495544446, -0.35489329934835645, 0.20326439933130835, -0.3548932993483582,
			-0.17647087614426926, -0.29673560066869165, 0.32352912385573074, 0.37870315946081057,

			// Liquids
			.09375, -.015349998474121662,

			// Climbing interactions
			0.1176000022888175, 0.07248412919149416, 0.11760000228882461, .47557591178630787, -.43050451123717437,
			-.2967356006687112, -0.21589434553872877,

			// Odd superflat
			-0.40739540236493843, 0.3331999936342238, 0.24813599859094593, -0.015555072702201134, -0.23152379758701125,
			-0.5546255304958936, -0.05462553049589314, -0.01555507270220069, -0.7170746714356024, 0.164773281826065,
			-.12129684053919476);

	@Override
	public String getCategory() {
		return "Step";
	}

	@Override
	public String getDebugName() {
		return "Step#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
