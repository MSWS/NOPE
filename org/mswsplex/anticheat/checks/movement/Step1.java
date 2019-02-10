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

public class Step1 implements Check, Listener {

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

		if (player.isFlying() || cp.hasMovementRelatedPotion())
			return;
		if (cp.timeSince("lastLiquid") < 500)
			return;

		if (cp.timeSince("lastSlimeBlock") < 1000)
			return;

		if (cp.timeSince("lastDamageTaken") < 500)
			return;

		if (cp.timeSince("lastVehicle") < 1000)
			return;

		if (cp.timeSince("lastBlockPlace") < 500)
			return;

		if (cp.timeSince("lastTeleport") < 500)
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() == from.getY())
			return;

		if (!cp.isOnGround() || cp.isInWeirdBlock())
			return;

		if (cp.isBlockAbove() && cp.distanceToGround() < 2)
			return;

		double diff = to.getY() - from.getY();

		boolean normal = false;

		for (double d : regular) {
			if (diff == d) {
				normal = true;
				break;
			}
		}

		if (normal)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&8" + diff);

		cp.flagHack(this, 5);
	}

	private double[] regular = {
			// Regular movement
			0.41999998688697815, -0.015555072702198913, -0.07840000152587834, 0.2000000476837016, 0.20000004768311896,
			0.12160004615724063, 0.20000004768371582, 0.20000004768371582, 0.2000000476836732, 0.20000004768365898,
			-0.07840000152587923, -0.07840000152587878, -0.35489329934835556, -0.10408037809303572, 0.40444491418477835,
			-.6517088341626156, .033890786745502055, -0.1216000461578366, -0.05489334703207138, 0.20000004768370871,
			0.6000000238418579,

			// Slab interactions
			.5, -0.03584062504455687, -0.6517088341626174, -0.0358406250445551, -0.6537296175885947,
			-0.1537296175885947,

			// Climbing interactions
			0.1176000022888175, 0.07248412919149416, 0.11760000228882461,

			// Odd superflat
			-0.40739540236493843, 0.3331999936342238, 0.24813599859094593, -0.015555072702201134, -0.23152379758701125,
			-0.5546255304958936, -0.05462553049589314, -0.01555507270220069, -0.7170746714356024, 0.164773281826065 };

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
