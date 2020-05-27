package xyz.msws.anticheat.checks.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

public class SafeWalk1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		Location to = event.getTo();
		Location from = event.getFrom();

		double dX = Math.abs(to.getX() - from.getX());
		double dZ = Math.abs(to.getZ() - from.getZ());

		double max = .00001;

		if ((dX > max && dZ > max) || (dX <= .1 && dZ <= .1))
			return;

		if (player.isSneaking() && !player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
			return;
		}

		if (player.getLocation().clone().add(0, -1, 0).getBlock().getType().isSolid())
			return;

		if (player.isFlying())
			return;

		if (player.getFallDistance() > 4)
			return;

		if (cp.timeSince(Stat.FLYING) < 300)
			return;

		if (cp.timeSince(Stat.CLIMBING) < 200)
			return;

		double yaw = Math.abs(to.getYaw()) % 90;

		if (yaw < 135 && yaw > 45)
			yaw = 90 - yaw;

		if (yaw < 2)
			return;

		boolean validSurrounding = false;

		String around = "";

		for (int x = -1; x <= 1; x += 1) {
			for (int z = -1; z <= 1; z += 1) {
				Block b = player.getLocation().clone().add(x, 0, z).getBlock();
				around += MSG.camelCase(b.getType() + " ");
				if (b.getType().isSolid()) {
					validSurrounding = true;
					break;
				}
			}
			if (validSurrounding)
				break;
		}

		if (validSurrounding)
			return;

		cp.flagHack(this, 5, String.format("Yaw: %f\ndX: %f\ndZ: %.2f\nBlocks: %s", yaw, dX, dZ, around));
	}

	@Override
	public String getCategory() {
		return "SafeWalk";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
