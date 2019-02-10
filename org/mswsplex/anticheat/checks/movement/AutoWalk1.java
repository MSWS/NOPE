package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class AutoWalk1 implements Check, Listener {

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

		if (player.isFlying() || player.isInsideVehicle())
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() < from.getY())
			return;

		cp.setTempData("lastWalkMove", (double) System.currentTimeMillis());

		if (cp.timeSince("lastWalkChat") > 50)
			return;
		cp.flagHack(this, 20);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (cp.timeSince("lastWalkMove") < 50)
			cp.setTempData("lastWalkChat", (double) System.currentTimeMillis());
	}

	@Override
	public String getCategory() {
		return "AutoWalk";
	}

	@Override
	public String getDebugName() {
		return "AutoWalk#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
