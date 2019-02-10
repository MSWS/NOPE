package org.mswsplex.anticheat.checks.client;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class NoGround1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.CLIENT;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (!player.isOnGround()) // If the client SAYS they're not on the ground, return
			return;

		if (cp.isOnGround())
			return;

		if (cp.timeSince("lastBlockPlace") < 1500)
			return;

		if (cp.timeSince("wasFlying") < 2000)
			return;

		if (player.getNearbyEntities(1, 2, 1).stream().anyMatch((entity) -> entity instanceof Boat))
			return;

		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "NoGround";
	}

	@Override
	public String getDebugName() {
		return "NoGround#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
