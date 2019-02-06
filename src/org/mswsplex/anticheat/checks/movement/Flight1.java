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

public class Flight1 implements Check, Listener {

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

		if (cp.isOnGround())
			return;
		if (player.isFlying() || cp.isInClimbingBlock() || player.isInsideVehicle())
			return;
		if (cp.timeSince("lastLiquid") < 400)
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() != from.getY())
			return;

		if (cp.timeSince("lastOnGround") <= 300)
			return;

		if (cp.timeSince("lastBlockPlace") < 1500)
			return;

		if (player.getVelocity().getY() > 0)
			return;

		cp.flagHack(this, 5);
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
