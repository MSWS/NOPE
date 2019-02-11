package org.mswsplex.anticheat.checks.client;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

/**
 * 
 * Explanation: Slightly teleports the player if they aren't moving every so
 * ticks and sees if (after one tick) the player has in fact actually teleported
 * 
 * @author imodm
 * 
 */
public class AntiRotate1 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.CLIENT;
	}

	private final int CHECK_PER = 200;

	@Override
	public void register(AntiCheat plugin) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				CPlayer cp = plugin.getCPlayer(player);
				if (player.getSpectatorTarget() != null)
					return;
				if (player.getOpenInventory() != null)
					return;
				if (cp.timeSince("lastMove") <= 100)
					return;
				Location modified = player.getLocation();
				float original = modified.getYaw();
				modified.setYaw(modified.getYaw() - .0001f);

				player.teleport(modified);

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
					if (player.getLocation().getYaw() != original)
						return;
					cp.flagHack(this, 50);
				}, 1);
			}
		}, CHECK_PER, CHECK_PER);
	}

	@Override
	public String getCategory() {
		return "AntiRotate";
	}

	@Override
	public String getDebugName() {
		return "AntiRotate#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
