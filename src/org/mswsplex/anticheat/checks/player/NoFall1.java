package org.mswsplex.anticheat.checks.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Uh I have no idea how this check works. Will likely be rewritten soon
 * 
 * @author imodm
 * @deprecated Outdated and unreliable - likely to be rewritten
 *
 */
@Deprecated
public class NoFall1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
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

		if (!cp.isOnGround())
			return;

		if (cp.isBlockNearby(Material.WEB) || cp.isBlockNearby(Material.WEB, 1.0))
			return;

		if (cp.isBlockNearby("SLAB") || cp.isBlockNearby("STEP") || cp.isInWeirdBlock())
			return;

		if (cp.timeSince("lastTeleport") < 500)
			return;

		if (player.getFallDistance() == 0)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "NoFall";
	}

	@Override
	public String getDebugName() {
		return "NoFall#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}

	@Override
	public boolean onlyLegacy() {
		// TODO Auto-generated method stub
		return false;
	}
}
